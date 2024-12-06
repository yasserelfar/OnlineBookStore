package com.mina.yasser;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductAuthor, etProductCategory, etProductPrice, etProductQuantity;
    private ProductDao productDao;
    private Button btnSaveProduct;  // Declare the button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        etProductName = findViewById(R.id.etProductName);
        etProductAuthor = findViewById(R.id.etProductAuthor);
        etProductCategory = findViewById(R.id.etProductCategory);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);

        btnSaveProduct = findViewById(R.id.btnSaveProduct);  // Initialize the button

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();

        // Set OnClickListener for the save button
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct(view);  // Call the saveProduct method when clicked
            }
        });
    }
    public String generateBarcode() {
        return UUID.randomUUID().toString();  // Generates a unique barcode
    }
    public void saveProduct(View view) {
        String name = etProductName.getText().toString();
        String author = etProductAuthor.getText().toString();
        String category = etProductCategory.getText().toString();
        double price = Double.parseDouble(etProductPrice.getText().toString());
        int quantity = Integer.parseInt(etProductQuantity.getText().toString());

        // Generate a unique barcode
        String barcode = generateBarcode();  // Use either UUID or Random barcode method

        Product newProduct = new Product();
        newProduct.setBarcode(barcode);
        newProduct.setName(name);
        newProduct.setAuthor(author);
        newProduct.setCategory(category);
        newProduct.setPrice(price);
        newProduct.setQuantityInStock(quantity);

        // Insert the new product into the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                productDao.insertProduct(newProduct);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddProductActivity.this, "Product added successfully with Barcode: " + barcode, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }).start();
    }

}
