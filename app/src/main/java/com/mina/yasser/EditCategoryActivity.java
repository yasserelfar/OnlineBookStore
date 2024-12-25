package com.mina.yasser;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.factory.CategoryFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditCategoryActivity extends AppCompatActivity {
    private Button btnSave;
    private EditText edtName;
    private CategoryDao categoryDao;
    private Category category;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        edtName = findViewById(R.id.edtCategoryName);
        btnSave = findViewById(R.id.btnSaveCategory);
        AppDatabase database = AppDatabase.getInstance(this);
        categoryDao = database.categoryDao();

        int id = getIntent().getIntExtra("CategoryId", -1);
        if (id == -1) {
            Toast.makeText(this, "Invalid Category ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchCatDetails(id);
        btnSave.setOnClickListener(v -> saveCatDetails());
    }

    private void fetchCatDetails(int id) {
        categoryDao.getCategoryById(id).observe(this, cat -> {
            if (cat != null) {
                LiveData<Category> sharedCategory = CategoryFactory.getCategory(cat.getName());
                sharedCategory.observe(this, sharedCat -> {
                    if (sharedCat != null) {
                        this.category = sharedCat;
                        edtName.setText(sharedCat.getName());
                    } else {
                        showErrorAndClose();
                    }
                });
            } else {
                showErrorAndClose();
            }
        });
    }

    private void showErrorAndClose() {
        Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveCatDetails() {
        if (category == null) {
            Toast.makeText(this, "Unable to save. Category not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedName = edtName.getText().toString().trim();
        if (updatedName.isEmpty() || updatedName.length() < 3) {
            Toast.makeText(this, "Category name must be at least 3 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        category.setName(updatedName);
        executor.execute(() -> {
            try {
                categoryDao.updateCategory(category);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Category updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                Log.e("EditCategory", "Error updating category with ID: " + category.getId(), e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to update Category. Please try again.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
