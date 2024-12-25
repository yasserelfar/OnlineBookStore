package com.mina.yasser;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.CategoryAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.factory.CategoryFactory;

import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {

    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        EditText edtCategoryName = findViewById(R.id.edtCategoryName);
        Button btnAddCategory = findViewById(R.id.btnAddCategory);
        RecyclerView categoryList = findViewById(R.id.categoryList);

        categoryList.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this);
        categoryList.setAdapter(categoryAdapter);

        // Initialize the database and CategoryFactory
        AppDatabase database = AppDatabase.getInstance(this);
        CategoryDao categoryDao = database.categoryDao();
        CategoryFactory.init(categoryDao);

        // Load Categories
        categoryDao.getAllCategories().observe(this, categories -> {
            List<Category> sharedCategories = new ArrayList<>();
            for (Category category : categories) {
                // Observe the shared instance of each category
                LiveData<Category> sharedCategoryLiveData = CategoryFactory.getCategory(category.getName());
                sharedCategoryLiveData.observe(this, sharedCategory -> {
                    if (sharedCategory != null && !sharedCategories.contains(sharedCategory)) {
                        sharedCategories.add(sharedCategory);
                        categoryAdapter.setCategoryList(sharedCategories);
                    }
                });
            }
        });

        // Add Category
        btnAddCategory.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            addCategory(name);
            edtCategoryName.setText("");
        });
    }

    private void addCategory(String name) {
        new Thread(() -> {
            Category category = new Category(name);
            AppDatabase.getInstance(this).categoryDao().insertCategory(category);
        }).start();
    }
}
