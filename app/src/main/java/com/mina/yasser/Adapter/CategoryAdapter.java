package com.mina.yasser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.ManageCategoriesActivity;
import com.mina.yasser.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private final Context context;
    private final CategoryDao categoryDao;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categoryDao = com.mina.yasser.DataBase.AppDatabase.getInstance(context).categoryDao();
    }

    public void setCategoryList(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.txtCategoryName.setText(category.getName());

        // Delete Category
        holder.btnDelete.setOnClickListener(v -> {
            // Run delete in a background thread
            new Thread(() -> {
                categoryDao.deleteCategory(category);
                ((ManageCategoriesActivity) context).runOnUiThread(() -> {
                    // Remove from the list
                    categoryList.remove(position);
                    // Notify adapter of the change
                    notifyItemRemoved(position);
                    // Notify that the item range has changed after removal
                    notifyItemRangeChanged(position, categoryList.size());
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
