package com.mina.yasser.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ProductDao {

    // Insert a new product
    @Insert
    void insertProduct(Product product);

    // Update an existing product
    @Update
    void updateProduct(Product product);

    // Delete a product
    @Delete
    void deleteProduct(Product product);

    // Get all products as LiveData
    @Query("SELECT * FROM product")
    LiveData<List<Product>> getAllProducts();

    // Get products by category as LiveData
    @Query("SELECT * FROM product WHERE category = :category")
    LiveData<List<Product>> getProductsByCategory(String category);
}
