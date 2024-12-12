package com.mina.yasser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mina.yasser.AddProductActivity;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.EditProductActivity;
import com.mina.yasser.ManageBooksActivity;
import com.mina.yasser.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private boolean isAdmin;
    private ProductDao productDao;
    private Context context;
    private  CategoryDao categoryDao;
    private LifecycleOwner lifecycleOwner;  // Add LifecycleOwner here

    // Update the constructor to accept lifecycleOwner
    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
        this.lifecycleOwner = lifecycleOwner;
    }
    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
    }
    public void setProductList(List<Product> products) {
        this.productList = products;  // Correctly update the list
        notifyDataSetChanged();       // Notify the adapter of data changes
    }


    // ViewHolder for User
    static class ProductUserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView,populartiy,author;
        ImageView productImageView;
        Button btnAddToCart;

        public ProductUserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            author=itemView.findViewById(R.id.productAuthor);
            populartiy=itemView.findViewById(R.id.productPopularity);
            productImageView = itemView.findViewById(R.id.productImage);  // ImageView for product image
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);  // Button for Add to Cart
        }
    }

    // ViewHolder for Admin
    static class ProductAdminViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, barcodeTextView,category;
        ImageView productImageView;
        Button btnEdit, btnDelete;

        public ProductAdminViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            barcodeTextView = itemView.findViewById(R.id.productBarcode);
            category=itemView.findViewById(R.id.productcategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isAdmin ? 1 : 0;  // Return different view types for admin and user
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_admin, parent, false);
            return new ProductAdminViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_user, parent, false);
            return new ProductUserViewHolder(itemView);
        }
    }

    @Override

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);

        if (holder instanceof ProductAdminViewHolder) {
            // Set product name, price, and barcode for admin view
            ((ProductAdminViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductAdminViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
            ((ProductAdminViewHolder) holder).barcodeTextView.setText("Barcode: " + product.getBarcode());
            ((ProductAdminViewHolder) holder).category.setText("Category: Loading..."); // Temporary loading text

            categoryDao = AppDatabase.getInstance(context).categoryDao(); // Ensure this is not null

            if (categoryDao != null && lifecycleOwner != null) {
                categoryDao.getCategoryById(product.getCategoryId()).observe(lifecycleOwner, new Observer<Category>() {
                    @Override
                    public void onChanged(Category category) {
                        if (category != null) {
                            ((ProductAdminViewHolder) holder).category.setText("Category: " + category.getName());
                        } else {
                            ((ProductAdminViewHolder) holder).category.setText("Category: Not Found");
                        }
                    }
                });
            } else {
                Log.e("ProductAdapter", "CategoryDao or lifecycleOwner is null, unable to fetch category.");
                ((ProductAdminViewHolder) holder).category.setText("Category: Error");
            }

            // Handle Edit and Delete buttons for admin
            ((ProductAdminViewHolder) holder).btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("barcode", product.getBarcode());
                context.startActivity(intent);
            });

            ((ProductAdminViewHolder) holder).btnDelete.setOnClickListener(v -> {
                deleteProduct(product);
            });
        }
        else{
            // Set product name and price for the user view
            ((ProductUserViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductUserViewHolder) holder).author.setText(product.getAuthor());
            ((ProductUserViewHolder) holder).populartiy.setText("popularity:"+product.getPopularity());
            ((ProductUserViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());

            // Handle Add to Cart button click
            ((ProductUserViewHolder) holder).btnAddToCart.setOnClickListener(v -> {
                // Implement the logic to add the product to cart here
                Toast.makeText(context, "Added to Cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to delete a product
    private void deleteProduct(Product product) {
        new Thread(() -> {
            productDao.deleteProduct(product);
            ((ManageBooksActivity) context).runOnUiThread(() -> {
                Toast.makeText(context, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                productList.remove(product);
                notifyDataSetChanged();
            });
        }).start();
    }
}
