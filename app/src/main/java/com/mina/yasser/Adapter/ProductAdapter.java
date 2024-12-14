package com.mina.yasser.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.EditProductActivity;
import com.mina.yasser.ManageBooksActivity;
import com.mina.yasser.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private boolean isAdmin;
    private ProductDao productDao;
    private Context context;
    private  CategoryDao categoryDao;
    private LifecycleOwner lifecycleOwner;  // Add LifecycleOwner here
    private CartDao cartDao;
    private Object holder;
    private SharedPreferences sharedPreferences;

    private int userId;

    // Update the constructor to accept lifecycleOwner
    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
        this.lifecycleOwner = lifecycleOwner;
        cartDao = AppDatabase.getInstance(context).cartDao();

    }
    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao, CartDao cartDao, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
        this.cartDao = cartDao; // Ensure CartDao is initialized here
        this.lifecycleOwner = lifecycleOwner;
        cartDao = AppDatabase.getInstance(context).cartDao();

    }

    public ProductAdapter(Context context, List<Product> productList, boolean isAdmin, ProductDao productDao) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.productList = productList;
        this.productDao = productDao;
        cartDao = AppDatabase.getInstance(context).cartDao();

    }
    public void setProductList(List<Product> products) {
        this.productList = products;  // Correctly update the list
        notifyDataSetChanged();       // Notify the adapter of data changes
    }


    // ViewHolder for User
    static class ProductUserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView,populartiy,author,edition;
        ImageView productImageView;
        Button btnAddToCart;

        public ProductUserViewHolder(View itemView) {

            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            author=itemView.findViewById(R.id.productAuthor);
            populartiy=itemView.findViewById(R.id.productPopularity);
            edition=itemView.findViewById(R.id.productEdition);
            productImageView = itemView.findViewById(R.id.productImage);  // ImageView for product image
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);  // Button for Add to Cart
        }
    }

    // ViewHolder for Admin
    static class ProductAdminViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, barcodeTextView,category,populartiy,author,edition;
        ImageView productImageView;
        Button btnEdit, btnDelete;

        public ProductAdminViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            author=itemView.findViewById(R.id.productAuthor);
            populartiy=itemView.findViewById(R.id.productPopularity);
            edition=itemView.findViewById(R.id.productEdition);
            priceTextView = itemView.findViewById(R.id.productPrice);
            barcodeTextView = itemView.findViewById(R.id.productBarcode);
            category=itemView.findViewById(R.id.productcategory);
            productImageView=itemView.findViewById(R.id.productImage);
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
            ((ProductAdminViewHolder) holder).productImageView.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
            ((ProductAdminViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
            ((ProductAdminViewHolder) holder).edition.setText("Edition: " + product.getEdition());
            ((ProductAdminViewHolder) holder).barcodeTextView.setText("Barcode: " + product.getBarcode());
            ((ProductAdminViewHolder) holder).category.setText("Category: Loading..."); // Temporary loading text
            ((ProductAdminViewHolder) holder).author.setText(product.getAuthor());
            ((ProductAdminViewHolder) holder).populartiy.setText("popularity:"+product.getPopularity());

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
           // Default -1 if not found

            // Set product name and price for the user view
            ((ProductUserViewHolder) holder).nameTextView.setText(product.getName());
            ((ProductUserViewHolder) holder).author.setText(product.getAuthor());
            ((ProductUserViewHolder) holder).populartiy.setText("popularity:"+product.getPopularity());
            ((ProductUserViewHolder) holder).productImageView.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
            ((ProductUserViewHolder) holder).edition.setText("productEdition :"+product.getEdition());
            ((ProductUserViewHolder) holder).priceTextView.setText("Price: $" + product.getPrice());
            // Handle Add to Cart button click

            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            // Get stored userId
            userId = sharedPreferences.getInt("userId", -1);
            ((ProductUserViewHolder) holder).btnAddToCart.setOnClickListener(v -> {
                addToCart(product,userId);
                Toast.makeText(context, "Added to Cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }
//    public void addToCart(String productBarcode, int quantity) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            if (cartDao == null) {
//                Log.e("ProductAdapter", "CartDao is null!");
//                return;
//            }
//
//
//
//            if (userId == -1) {
//                // Handle error: User not logged in
//                return;
//            }
//
//            Cart existingCart = cartDao.getCartProduct(userId, productBarcode);
//            if (existingCart != null) {
//                // Update existing cart item
//                cartDao.updateQuantity(userId, productBarcode, existingCart.getQuantity() + quantity);
//                Log.d("Cart", "Updated product in cart");
//            } else {
//                // Insert new product into cart
//                Cart newCart = new Cart();
//                newCart.setUserId(userId);
//                newCart.setProductBarcode(productBarcode);
//                newCart.setQuantity(quantity);
//                cartDao.insertProduct(newCart);
//                Log.d("Cart", "Inserted new product into cart");
//            }
//
//
//        });
//    }


    private void addToCart(Product product, int userId) {

//            int userId = sharedPreferences.getInt("userId", -1);
        // Use ExecutorService to perform database operations off the main thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // Perform the database operations in the background thread
            Cart existingCartItem = cartDao.getCartItemByProduct(userId, product.getBarcode()).getValue();

            if (existingCartItem != null) {
                // If the product is already in the cart, update the quantity
                existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                cartDao.updateCart(existingCartItem);
            } else {
                // If the product is not in the cart, add a new item
                Cart newCartItem = new Cart();
                newCartItem.setUserId(userId);
                newCartItem.setProductBarcode(product.getBarcode());
                newCartItem.setQuantity(1);
//                cartDao.insertProduct(newCartItem);
                Executors.newSingleThreadExecutor().execute(() -> {
                    cartDao.insertProduct(newCartItem);
                    Log.d("AddToCart", "Inserted cart: userId=" + userId + ", barcode=" + product.getBarcode());
                    Log.d("AddToCart", "User ID: " + userId + ", Cart Item: " + newCartItem.getCartId());
                });
            }

            // Post the Toast message on the main thread
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                Log.d("AddToCart", "User ID: " + userId);
                Log.d("CartActivity", "User ID: " + userId);
                Log.d("AddToCart", "Cart item added: userId=" + userId + ", barcode=" + product.getBarcode());
                Toast.makeText(context, "Added to Cart: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
        });
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
