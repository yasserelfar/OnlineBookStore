package com.mina.yasser.factory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CategoryFactory {
    private static final Map<String, Category> categoryCache = new ConcurrentHashMap<>();
    private static CategoryDao categoryDao;

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
            categoryLiveData.observeForever(category -> {
                if (category != null) {
                    categoryCache.put(name, category); // Cache the fetched category
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

    // Add a new category to the database and cache
    public static void addCategory(String name) {
        if (!categoryCache.containsKey(name)) {
            new Thread(() -> {
                Category category = new Category(name);
                categoryDao.insertCategory(category);
                categoryCache.put(name, category); // Add to cache
            }).start();
        }
    }
}
