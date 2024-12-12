package com.mina.yasser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.ViewModel.CategoryViewModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etProductName, etProductAuthor, etProductPrice, etProductQuantity,etProductEdition;
    private Spinner spinnerCategory;
    private Button btnSaveProduct, btnSelectImage;
    private ImageView ivProductImage;
    private String selectedCategory = null;
    private byte[] productImage;

    private ProductDao productDao;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        etProductName = findViewById(R.id.etProductName);
        etProductAuthor = findViewById(R.id.etProductAuthor);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductEdition = findViewById(R.id.etProductEdition);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivProductImage = findViewById(R.id.ivProductImage);

        // Initialize database and DAOs
        productDao = AppDatabase.getInstance(this).productDao();

        // Initialize CategoryViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Load categories into spinner
        loadCategoriesIntoSpinner();

        // Set OnItemSelectedListener for category selection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Set OnClickListener for the save button
        btnSaveProduct.setOnClickListener(view -> saveProduct());

        // Set OnClickListener for the select image button
        btnSelectImage.setOnClickListener(view -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ivProductImage.setImageBitmap(bitmap);

                // Convert Bitmap to byte[]
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                productImage = outputStream.toByteArray();
            } catch (Exception e) {
                Log.e("ImagePickerError", "Error selecting image", e);
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadCategoriesIntoSpinner() {
        categoryViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                    }

                    // Populate the spinner with category names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(AddProductActivity.this, "No categories available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String generateBarcode() {
        return UUID.randomUUID().toString();
    }

    private void saveProduct() {
        String name = etProductName.getText().toString();
        String author = etProductAuthor.getText().toString();
        String priceText = etProductPrice.getText().toString();
        String quantityText = etProductQuantity.getText().toString();
        String editionText = etProductEdition.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) ||
                TextUtils.isEmpty(priceText) || TextUtils.isEmpty(quantityText) ||
                selectedCategory == null || productImage == null) {
            Toast.makeText(this, "Please fill in all fields and select a category and image", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Debug", "Inputs - Name: " + name + ", Author: " + author + ", Price: " + priceText +
                ", Quantity: " + quantityText + ", Edition: " + editionText + ", Category: " + selectedCategory);

        try {
            double edition = Double.parseDouble(editionText);
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            categoryViewModel.getCategoryByName(selectedCategory).observe(this, category -> {
                if (category != null) {
                    Product newProduct = new Product();
                    newProduct.setName(name);
                    newProduct.setAuthor(author);
                    newProduct.setPrice(price);
                    newProduct.setQuantityInStock(quantity);
                    newProduct.setCategoryId(category.getId());
                    newProduct.setBarcode(generateBarcode());
                    newProduct.setImage(productImage);
                    newProduct.setEdition(edition);

                    new Thread(() -> {
                        try {
                            productDao.insertProduct(newProduct);
                            runOnUiThread(() -> Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show());
                            finish();
                        } catch (Exception e) {
                            Log.e("ProductInsertError", "Error inserting product", e);
                            runOnUiThread(() -> Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                } else {
                    Log.e("Debug", "Category not found: " + selectedCategory);
                    Toast.makeText(AddProductActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Log.e("Debug", "Invalid input format", e);
            Toast.makeText(this, "Invalid price, edition, or quantity format", Toast.LENGTH_SHORT).show();
        }
    }

}
