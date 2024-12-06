package com.mina.yasser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.AddProductActivity;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.EditProductActivity;
import com.mina.yasser.ManageBooksActivity;
import com.mina.yasser.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private boolean isAdmin;  // Variable to distinguish between admin and regular user
    private ProductDao productDao;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    // ViewHolder for User
    static class ProductUserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;

        public ProductUserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
        }
    }

    // ViewHolder for Admin
    static class ProductAdminViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;
        Button btnEdit, btnDelete;
        TextView barcodeTextView;

        public ProductAdminViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            barcodeTextView = itemView.findViewById(R.id.productBarcode);  // Link barcode view
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Return different view types for admin and user
        return isAdmin ? 1 : 0;  // 0 = User view, 1 = Admin view
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            // Admin view
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_admin, parent, false);
            return new ProductAdminViewHolder(itemView);
        } else {
            // User view
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_user, parent, false);
            return new ProductUserViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);

        if (holder instanceof ProductUserViewHolder) {
            ((ProductUserViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductUserViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
        } else if (holder instanceof ProductAdminViewHolder) {
            ((ProductAdminViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductAdminViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
            ((ProductAdminViewHolder) holder).barcodeTextView.setText("Barcode: " + product.getBarcode());  // Show barcode

            // Admin-specific actions like Edit, Delete
            ((ProductAdminViewHolder) holder).btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("barcode", product.getBarcode());
                context.startActivity(intent);
            });

            ((ProductAdminViewHolder) holder).btnDelete.setOnClickListener(v -> {
                deleteProduct(product);
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    // Method to delete a product
    private void deleteProduct(Product product) {
        new Thread(() -> {
            productDao.deleteProduct(product);
            // Notify the UI thread to update the list after deleting
            ((ManageBooksActivity) context).runOnUiThread(() -> {
                Toast.makeText(context, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                // Remove the product from the list and update the RecyclerView
                productList.remove(product);
                notifyDataSetChanged();
            });
        }).start();
    }
}
