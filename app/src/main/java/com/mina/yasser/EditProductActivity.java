package com.mina.yasser;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

public class EditProductActivity extends AppCompatActivity {

    private EditText edtName, edtPrice, edtQuantity;
    private Button btnSave;
    private ProductDao productDao;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize views
        edtName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtProductPrice);
        edtQuantity = findViewById(R.id.edtProductQuantity);
        btnSave = findViewById(R.id.btnSaveProduct);

        // Initialize the database and ProductDao
        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();

        // Get the barcode passed from the previous activity
        String barcode = getIntent().getStringExtra("barcode");

        // Fetch the product by barcode (this should match the primary key)
        new Thread(() -> {
            product = productDao.getProductByBarcode(barcode); // Use barcode to query
            runOnUiThread(() -> {
                if (product != null) {
                    edtName.setText(product.getName());
                    edtPrice.setText(String.valueOf(product.getPrice()));
                    edtQuantity.setText(String.valueOf(product.getQuantityInStock()));
                } else {
                    Toast.makeText(EditProductActivity.this, "Product not found!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

        // Save the updated product details when Save button is clicked
        btnSave.setOnClickListener(v -> {
            String updatedName = edtName.getText().toString().trim();
            String updatedPrice = edtPrice.getText().toString().trim();
            String updatedQuantity = edtQuantity.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedPrice.isEmpty() && !updatedQuantity.isEmpty()) {
                product.setName(updatedName);
                product.setPrice(Double.parseDouble(updatedPrice));
                product.setQuantityInStock(Integer.parseInt(updatedQuantity));

                new Thread(() -> {
                    productDao.updateProduct(product);  // Update the product in the database
                    runOnUiThread(() -> {
                        Toast.makeText(EditProductActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the edit screen
                    });
                }).start();
            } else {
                Toast.makeText(EditProductActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
