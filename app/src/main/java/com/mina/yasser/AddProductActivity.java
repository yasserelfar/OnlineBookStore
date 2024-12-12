package com.mina.yasser;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.ViewModel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductAuthor, etProductPrice, etProductQuantity;
    private Spinner spinnerCategory;
    private Button btnSaveProduct;
    private String selectedCategory = null;

    private ProductDao productDao;
    private CategoryViewModel categoryViewModel; // ViewModel for categories

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        etProductName = findViewById(R.id.etProductName);
        etProductAuthor = findViewById(R.id.etProductAuthor);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        // Initialize database and DAOs
        productDao = AppDatabase.getInstance(this).productDao();

        // Initialize CategoryViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Load categories into spinner
        loadCategoriesIntoSpinner();

        // Set OnItemSelectedListener for category selection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Set OnClickListener for the save button
        btnSaveProduct.setOnClickListener(view -> saveProduct());
    }

    private void loadCategoriesIntoSpinner() {
        categoryViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        categoryNames.add(category.getName());  // Assuming getName() exists in your Category class
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
        return UUID.randomUUID().toString();  // Generates a unique barcode
    }

    private void saveProduct() {
        String name = etProductName.getText().toString();
        String author = etProductAuthor.getText().toString();
        String priceText = etProductPrice.getText().toString();
        String quantityText = etProductQuantity.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) ||
                TextUtils.isEmpty(priceText) || TextUtils.isEmpty(quantityText) ||
                selectedCategory == null) {
            Toast.makeText(this, "Please fill in all fields and select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            // Find category by name and save product
            categoryViewModel.getCategoryByName(selectedCategory).observe(this, new Observer<Category>() {
                @Override
                public void onChanged(Category category) {
                    if (category != null) {
                        Product newProduct = new Product();
                        newProduct.setName(name);
                        newProduct.setAuthor(author);
                        newProduct.setPrice(price);
                        newProduct.setQuantityInStock(quantity);
                        newProduct.setCategoryId(category.getId());
                        String barcode = generateBarcode();
                        newProduct.setBarcode(barcode);
                        // Insert product into the database
                        new Thread(() -> {
                            try {
                                productDao.insertProduct(newProduct);
                                runOnUiThread(() -> {
                                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } catch (Exception e) {
                                Log.e("ProductInsertError", "Error inserting product", e);
                                runOnUiThread(() -> {
                                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();

                    } else {
                        Toast.makeText(AddProductActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity format", Toast.LENGTH_SHORT).show();
        }
    }
}
