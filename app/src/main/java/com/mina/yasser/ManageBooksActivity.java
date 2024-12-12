package com.mina.yasser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.ProductAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

import java.util.ArrayList;
import java.util.List;
public class ManageBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private ProductAdapter productAdapter;
    private ProductDao productDao;
    private Button btnAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_books);

        // Initialize views
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        btnAddBook = findViewById(R.id.btnAddBook);

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();

        // Check if the user is an admin
        boolean isAdmin = checkIfAdmin();  // Implement this method to check if the user is admin

        // Set up RecyclerView
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter (pass empty list initially, will be updated later)
        productAdapter = new ProductAdapter(this, new ArrayList<>(), isAdmin, productDao, this);  // Pass 'this' as the LifecycleOwner
        recyclerViewBooks.setAdapter(productAdapter);

        // Observe changes in the product list and update the adapter
        productDao.getAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                // Update the product list in the adapter when data changes
                productAdapter.setProductList(products);
            }
        });

        // Add button listener to navigate to AddProductActivity
        btnAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(ManageBooksActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkIfAdmin() {
        // Replace with actual logic to check if the user is an admin (e.g., using SharedPreferences)
        return true;  // Just for demonstration, assuming the user is an admin
    }
}
