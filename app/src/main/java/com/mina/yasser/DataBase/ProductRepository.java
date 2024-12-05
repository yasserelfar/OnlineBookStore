package com.mina.yasser.DataBase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProductRepository {
    private ProductDao productDao;

    public ProductRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        productDao = database.productDao();
    }

    // Get all products as LiveData
    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();  // Make sure this returns LiveData
    }

    // Get products by category as LiveData
    public LiveData<List<Product>> getProductsByCategory(String category) {
        return productDao.getProductsByCategory(category);  // Make sure this returns LiveData
    }
}
