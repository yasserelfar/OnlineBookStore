package com.mina.yasser.factory;

import androidx.lifecycle.LiveData;

import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CategoryFactory {
    private static final Map<String, Category> categoryCache = new ConcurrentHashMap<>();
    private static CategoryDao categoryDao;
    public static List<String> convertToCategoryNames(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());  // Add each category's name to the list
        }
        return categoryNames;
    }
    // Pass the CategoryDao so it can fetch the actual category from the database
    public static void init(CategoryDao dao) {
        categoryDao = dao;
    }

    public static LiveData<Category> getCategory(String name) {
        // Check if the category is already cached
        if (!categoryCache.containsKey(name)) {
            // Fetch the category from the database asynchronously
            LiveData<Category> categoryLiveData = categoryDao.getCategoriesByName(name);
            categoryLiveData.observeForever(category -> {
                // Cache the fetched category if not null
                if (category != null) {
                    categoryCache.put(name, category);
                }
            });
            return categoryLiveData;
        } else {
            // If category is cached, return a LiveData with the cached category
            return new LiveData<Category>() {
                @Override
                protected void onActive() {
                    super.onActive();
                    postValue(categoryCache.get(name));
                }
            };
        }
    }
}
