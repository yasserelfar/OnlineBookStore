package com.mina.yasser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.CheckoutAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.DataBase.UserDao;
import com.mina.yasser.ViewModel.CheckoutViewModel;
import com.mina.yasser.factory.CheckoutViewModelFactory;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private CheckoutViewModel checkoutViewModel;
    private TextView checkoutTotalPrice;
    private RecyclerView cartRecyclerView;
    private Button placeOrderButton;
    private UserDao userDao;
    private CartDao cartDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    int userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize DAOs
        userDao = AppDatabase.getInstance(this).userDao();
        cartDao = AppDatabase.getInstance(this).cartDao();
        orderDao = AppDatabase.getInstance(this).orderDao();
        productDao = AppDatabase.getInstance(this).productDao();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Get stored userId
        userId = sharedPreferences.getInt("userId", -1);

        // Initialize ViewModel using the factory
        checkoutViewModel = new ViewModelProvider(this,
                new CheckoutViewModelFactory(cartDao, productDao, orderDao,userDao, userId))
                .get(CheckoutViewModel.class);

        // Initialize UI components
        checkoutTotalPrice = findViewById(R.id.checkoutTotalPrice);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe cart LiveData
        checkoutViewModel.getCartLiveData().observe(this, cartList -> {
            if (cartList != null && !cartList.isEmpty()) {
                // Update RecyclerView with cart data
                CheckoutAdapter cartAdapter = new CheckoutAdapter(cartList, productDao, cartDao, this);
                cartRecyclerView.setAdapter(cartAdapter);
                placeOrderButton.setEnabled(true);
            } else {
                // Handle empty cart case
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                placeOrderButton.setEnabled(false);
            }
        });

        // Observe total price LiveData
        checkoutViewModel.getTotalPriceLiveData().observe(this, totalPrice -> {
            checkoutTotalPrice.setText("Total: $" + String.format("%.2f", totalPrice));
        });

        // Handle Place Order button click
        placeOrderButton.setOnClickListener(v -> {
            // Get current cart data and total price
            List<Cart> currentCart = checkoutViewModel.getCartLiveData().getValue();
            Double totalPrice = checkoutViewModel.getTotalPriceLiveData().getValue();

            if (currentCart != null && totalPrice != null) {
                checkoutViewModel.addOrderToDatabase(currentCart, totalPrice);
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                // After placing the order, refresh the data (clear cart)
                checkoutViewModel.refreshData();
                Intent intent = new Intent(this, AccountManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);// This will clear the cart and update the UI
            } else {
                Toast.makeText(this, "Error placing order. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
