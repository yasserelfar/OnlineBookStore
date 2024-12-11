package com.mina.yasser.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.DataBase.AppDatabase;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(Application application) {
        super(application);

        // Initialize the CategoryDao and get all categories as LiveData
        AppDatabase database = AppDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();  // Get all categories as LiveData
    }

    // Getter method for all categories
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
    // Method to get a category by its ID
    public LiveData<Category> getCategoryById(int categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }
    // Getter method for a category by name
    public LiveData<Category> getCategoryByName(String name) {
        return categoryDao.getCategoriesByName(name);  // Returns LiveData<Category>
    }

}
