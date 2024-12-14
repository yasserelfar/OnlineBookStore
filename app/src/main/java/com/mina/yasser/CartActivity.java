package com.mina.yasser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.CartAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Cart> cartList = new ArrayList<>();
    private CartDao cartDao;
    private ProductDao productDao;
    private SharedPreferences sharedPreferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Get stored userId
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            // If userId is not available, redirect to LoginActivity
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DAOs
        cartDao = AppDatabase.getInstance(this).cartDao();
        productDao = AppDatabase.getInstance(this).productDao();

        Button checkoutButton = findViewById(R.id.checkoutButton);
        loadCartItems();
        checkoutButton.setOnClickListener(v -> proceedToCheckout());

        // Load cart items
    }

    private void proceedToCheckout() {
        // Assuming you have a cart ID or an identifier for the entire cart (e.g., cartId)
        // Here we use the first cart item's ID, you can adjust based on your requirement.
        if (!cartList.isEmpty()) {
            long cartId = cartList.get(0).getCartId(); // Adjust this as per your Cart model

            // Navigate to CheckoutActivity with total price, userId, and cartId
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);

            startActivity(intent);
        } else {
            Toast.makeText(this, "Cart is empty. Please add items to the cart.", Toast.LENGTH_SHORT).show();
        }
    }



    private void loadCartItems() {
        // Observe cart items for the user
        cartDao.getCartProducts(userId).observe(this, carts -> {
            cartList=carts;
            if (carts != null && !carts.isEmpty()) {
                Log.d("CartActivity", "Fetched cart items: " + carts);
                cartAdapter = new CartAdapter(carts, productDao, cartDao, CartActivity.this);
                cartRecyclerView.setAdapter(cartAdapter);
            } else {
                Log.d("CartActivity", "Cart is empty for userId=" + userId);
                Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void logout() {
        // Clear SharedPreferences
        sharedPreferences.edit().clear().apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        finish();
    }
}
