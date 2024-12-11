package com.mina.yasser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mina.yasser.AddProductActivity;
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
        TextView nameTextView, priceTextView;
        ImageView productImageView;
        Button btnAddToCart;

        public ProductUserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            productImageView = itemView.findViewById(R.id.productImage);  // ImageView for product image
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);  // Button for Add to Cart
        }
    }

    // ViewHolder for Admin
    static class ProductAdminViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, barcodeTextView;
        ImageView productImageView;
        Button btnEdit, btnDelete;

        public ProductAdminViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            barcodeTextView = itemView.findViewById(R.id.productBarcode);
            productImageView = itemView.findViewById(R.id.productImage);  // ImageView for product image
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

        if (holder instanceof ProductUserViewHolder) {
            // Set product name and price for the user view
            ((ProductUserViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductUserViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());

            // Load product image using Glide

            // Handle Add to Cart button click
            ((ProductUserViewHolder) holder).btnAddToCart.setOnClickListener(v -> {
                // Implement the logic to add the product to cart here
                Toast.makeText(context, "Added to Cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            });

        } else if (holder instanceof ProductAdminViewHolder) {
            // Set product name, price, and barcode for admin view
            ((ProductAdminViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductAdminViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
            ((ProductAdminViewHolder) holder).barcodeTextView.setText("Barcode: " + product.getBarcode());


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
