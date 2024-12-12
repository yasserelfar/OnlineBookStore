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
        return productDao.getAllProducts();
    }

    // Get products by category as LiveData
    public LiveData<List<Product>> getProductsByCategory(int category) {
        return productDao.getProductsByCategory(category);
    }

    // Get products by author as LiveData
    public LiveData<List<Product>> getProductsByAuthor(String author) {
        return productDao.getProductsByAuthor(author);
    }

    // Get the top N popular products as LiveData
    public LiveData<List<Product>> getTopPopularProducts(int limit) {
        return productDao.getTopPopularProducts(limit);
    }

    // Insert a new product
    public void insertProduct(Product product) {
        new Thread(() -> productDao.insertProduct(product)).start();
    }

    // Update an existing product
    public void updateProduct(Product product) {
        new Thread(() -> productDao.updateProduct(product)).start();
    }

    // Delete a product
    public void deleteProduct(Product product) {
        new Thread(() -> productDao.deleteProduct(product)).start();
    }

    // Search products by title or author
    public LiveData<List<Product>> searchByTitleOrAuthor(String query) {
        return productDao.searchByTitleOrAuthor(query);
    }

    // Get a product by barcode
    public LiveData<Product> getProductByBarcode(String barcode) {
        return productDao.getProductByBarcode(barcode);
    }
}
