package com.mina.yasser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mina.yasser.DataBase.CartManager;
import com.mina.yasser.Adapter.CartAdapter;
import android.content.Intent;
import java.io.Serializable;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        checkOutButton = findViewById(R.id.btnCheckout);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems());
        cartRecyclerView.setAdapter(cartAdapter);

        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            Toast.makeText(this, "Checkout Successful!", Toast.LENGTH_SHORT).show();
            CartManager.getInstance().clearCart();
            cartAdapter.notifyDataSetChanged();
            finish();
        });
        checkOutButton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cartItems", (Serializable) CartManager.getInstance().getCartItems());
            startActivity(intent);
        });

    }
}
