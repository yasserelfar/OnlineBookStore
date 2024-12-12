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

import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {

    private CategoryDao categoryDao;
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

        AppDatabase database = AppDatabase.getInstance(this);
        categoryDao = database.categoryDao();

        // Load Categories
        LiveData<List<Category>> allCategories = categoryDao.getAllCategories();
        allCategories.observe(this, categories -> categoryAdapter.setCategoryList(categories));

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
            categoryDao.insertCategory(category);
        }).start();
    }
}
