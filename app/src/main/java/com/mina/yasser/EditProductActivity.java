package com.mina.yasser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.ViewModel.CategoryViewModel;
import com.mina.yasser.factory.CategoryFactory;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Initialize the Executor at the class level

import java.util.List;

public class EditProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner spinnerCategory;
    private EditText edtName, edtPrice, edtQuantity,edtEdition;
    private Button btnSave,btnSelectImage;
    private ProductDao productDao;
    private Product product;
    private ArrayAdapter<String> categoryAdapter;
    private ImageView ivProductImage;
    private byte[] productImage;
    private CategoryDao categoryDao;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize views
        edtName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtProductPrice);
        edtQuantity = findViewById(R.id.edtProductQuantity);
        btnSave = findViewById(R.id.btnSaveProduct);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        edtEdition = findViewById(R.id.edtProductEdition);
        ivProductImage = findViewById(R.id.edtProductImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        // Initialize the database and DAOs
        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();
        categoryDao = database.categoryDao();

        // Initialize the CategoryViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Populate the spinner with categories
        populateCategorySpinner();

        // Get the product barcode passed from the previous activity
        String barcode = getIntent().getStringExtra("barcode");

        // Fetch the product details and populate fields
        fetchProductDetails(barcode);

        // Set up save button functionality
        btnSave.setOnClickListener(v -> saveProductDetails());
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

    private void populateCategorySpinner() {
        categoryViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    // Convert the category objects to category names using the factory method
                    List<String> categoryNames = CategoryFactory.convertToCategoryNames(categories);
                    categoryAdapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
                } else {
                    Toast.makeText(EditProductActivity.this, "No categories available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchProductDetails(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            Toast.makeText(this, "Invalid product identifier", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observe LiveData for product details
        productDao.getProductByBarcode(barcode).observe(this, product -> {
            if (product != null) {
                this.product = product; // Ensure the product is set
                edtName.setText(product.getName());
                edtPrice.setText(String.valueOf(product.getPrice()));
                edtQuantity.setText(String.valueOf(product.getQuantityInStock()));
                edtEdition.setText(String.valueOf(product.getEdition()));
                ivProductImage.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
                productImage=product.getImage();
                         // Populate category spinner after product details are fetched
                if (categoryAdapter != null) {
                    categoryViewModel.getCategoryById(product.getCategoryId()).observe(this, new Observer<Category>() {
                        @Override
                        public void onChanged(Category category) {
                            if (category != null) {
                                int position = categoryAdapter.getPosition(category.getName());
                                if (position >= 0) {
                                    spinnerCategory.setSelection(position);  // Set the correct category
                                }
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



    // Initialize the Executor at the class level
    private Executor executor = Executors.newSingleThreadExecutor();

    private void saveProductDetails() {
        if (product == null) {
            Toast.makeText(this, "Unable to save. Product not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedName = edtName.getText().toString().trim();
        String updatedPrice = edtPrice.getText().toString().trim();
        String updatedQuantity = edtQuantity.getText().toString().trim();
        String updatedCategory = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : null;
        String updatedEdition = edtEdition.getText().toString().trim();
        // Validate fields
        if (updatedName.isEmpty() || updatedPrice.isEmpty() || updatedQuantity.isEmpty() || updatedCategory == null) {
            Toast.makeText(this, "Please fill in all fields and select a category.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Ensure valid price and quantity

        double price,edition;
        int quantity;

        // Ensure valid price and quantity
        try {
            price = Double.parseDouble(updatedPrice);
            edition = Double.parseDouble(updatedEdition);
            quantity = Integer.parseInt(updatedQuantity);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity format.", Toast.LENGTH_SHORT).show();
            Log.e("SaveProductDetails", "Error parsing price or quantity", e); // Log the exception for debugging
            return;
        }

        // Retrieve category by name and observe the result
        if (categoryViewModel != null) {
            try {
                categoryViewModel.getCategoryByName(updatedCategory).observe(this, new Observer<Category>() {
                    @Override
                    public void onChanged(Category category) {
                        if (category != null) {
                            // Update product details
                            product.setName(updatedName);
                            product.setPrice(price);
                            product.setQuantityInStock(quantity);
                            product.setCategoryId(category.getId());  // Set the correct category ID
                            product.setEdition(edition);
                            product.setImage(productImage);
                            // Run database operation on a background thread using Executor
                            executor.execute(() -> {
                                try {
                                    productDao.updateProduct(product); // Update the product in the database
                                    runOnUiThread(() -> {
                                        Toast.makeText(EditProductActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity
                                    });
                                } catch (Exception e) {
                                    Log.e("ProductUpdateError", "Error updating product", e);
                                    runOnUiThread(() -> {
                                        Toast.makeText(EditProductActivity.this, "Failed to update product. Please try again.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(EditProductActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                            Log.w("SaveProductDetails", "Category not found: " + updatedCategory); // Log a warning if category is not found
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error retrieving category. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("SaveProductDetails", "Error retrieving category", e); // Log the exception for debugging
            }
        } else {
            Toast.makeText(this, "CategoryViewModel is not initialized.", Toast.LENGTH_SHORT).show();
            Log.e("SaveProductDetails", "CategoryViewModel is null");
        }
    }

}
