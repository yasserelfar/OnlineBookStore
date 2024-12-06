package com.mina.yasser;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

import java.util.ArrayList;
import java.util.List;
import com.mina.yasser.Adapter.ProductAdapter;
public class HomeActivity extends AppCompatActivity {

    private ProductDao productDao;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        // Initialize the RecyclerView
        RecyclerView productList = findViewById(R.id.productList);
        productList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>());
        productList.setAdapter(productAdapter);

        // Initialize the database
        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();




        // Observe the product list
        LiveData<List<Product>> allProducts = productDao.getAllProducts();
        allProducts.observe(this, products -> {
            if (products != null) {
                productAdapter.setProductList(products); // Update RecyclerView with new data
            } else {
                Log.d("HomeActivity", "No products found");
            }
        });
    }
}
