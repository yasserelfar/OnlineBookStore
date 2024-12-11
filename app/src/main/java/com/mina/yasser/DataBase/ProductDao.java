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

    // Get products by author as LiveData
    @Query("SELECT * FROM product WHERE author = :author")
    LiveData<List<Product>> getProductsByAuthor(String author);

    // Get products sorted by popularity in descending order
    @Query("SELECT * FROM product ORDER BY popularity DESC LIMIT :limit")
    LiveData<List<Product>> getTopPopularProducts(int limit);

    // Get products by category as LiveData
    @Query("SELECT * FROM product WHERE category = :category")
    LiveData<List<Product>> getProductsByCategory(String category);

    // Get a product by barcode
    @Query("SELECT * FROM product WHERE barcode = :barcode LIMIT 1")
    Product getProductByBarcode(String barcode);
    @Query("SELECT * FROM product WHERE name LIKE :query OR author LIKE :query")
    List<Product> searchByTitleOrAuthor(String query);



}
