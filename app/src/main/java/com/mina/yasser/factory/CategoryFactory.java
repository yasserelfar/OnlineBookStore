package com.mina.yasser.factory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CategoryFactory {
    private static final Map<String, Category> categoryCache = new ConcurrentHashMap<>();
    private static CategoryDao categoryDao;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Single thread executor for background tasks

    // Initialize the factory with a DAO for database operations
    public static void init(CategoryDao dao) {
        categoryDao = dao;
    }

    // Convert a list of categories to their names
    public static List<String> convertToCategoryNames(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }
        return categoryNames;
    }

    // Flyweight: Get or create a shared Category object
    public static LiveData<Category> getCategory(String name) {
        // Check if the category is already cached
        if (!categoryCache.containsKey(name)) {
            // Fetch the category from the database asynchronously
            LiveData<Category> categoryLiveData = categoryDao.getCategoriesByName(name);
            categoryLiveData.observeForever(new Observer<Category>() {
                @Override
                public void onChanged(Category category) {
                    if (category != null) {
                        categoryCache.put(name, category);
                        categoryLiveData.removeObserver(this);
                    }
                }
            });

            return categoryLiveData;
        } else {
            // If cached, return the shared instance
            MutableLiveData<Category> cachedCategory = new MutableLiveData<>();
            cachedCategory.postValue(categoryCache.get(name));
            return cachedCategory;
        }
    }
    public static List<Category> getSharedCategories(List<Category> categories) {
        List<Category> sharedCategories = new ArrayList<>();
        for (Category category : categories) {
            if (!categoryCache.containsKey(category.getName())) {
                categoryCache.put(category.getName(), category);
            }
            sharedCategories.add(categoryCache.get(category.getName()));
        }
        return sharedCategories;
    }

    // Add a new category to the database and cache
    public static synchronized void addCategory(String name) {
        if (!categoryCache.containsKey(name)) {
            // Run the database insertion on a background thread
            executorService.execute(() -> {
                Category category = new Category(name);
                categoryDao.insertCategory(category);  // Insert in background thread
                categoryCache.put(name, category);     // Cache the category after insertion
            });
        }
    }


}
