package com.mina.yasser.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT name FROM categories")
    List<String> getAllCategoryNames();

    // Add this method to retrieve a category by its ID

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    LiveData<Category> getCategoriesByName(String name);
    @Query("SELECT * FROM product WHERE barcode = :barcode LIMIT 1")
    LiveData<Product> getCategoryByBarcode(String barcode);
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);
    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    LiveData<Category> getCategoryById(int categoryId);
    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    Category getCatById(int categoryId);

}
