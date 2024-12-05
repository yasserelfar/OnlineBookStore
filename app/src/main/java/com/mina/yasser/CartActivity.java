package com.mina.yasser;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data for the cart
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem("Book 1", 100, R.drawable.ic_launcher_foreground));
        cartItems.add(new CartItem("Book 2", 150, R.drawable.ic_launcher_foreground));
        cartItems.add(new CartItem("Book 3", 200, R.drawable.ic_launcher_foreground));

        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);
    }

    public void checkout(View view) {
        Toast.makeText(this, "Checkout not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    // Inner class for the CartItem
    public static class CartItem {
        private String name;
        private int price;
        private int imageResId;

        public CartItem(String name, int price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getImageResId() {
            return imageResId;
        }
    }

    // Inner class for the CartAdapter
    public static class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<CartItem> cartItems;
        private Context context;

        public CartAdapter(List<CartItem> cartItems, Context context) {
            this.cartItems = cartItems;
            this.context = context;
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cart_item_layout, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            CartItem item = cartItems.get(position);
            holder.itemName.setText(item.getName());
            holder.itemPrice.setText("Price: $" + item.getPrice());
            holder.itemImage.setImageResource(item.getImageResId());

            holder.removeButton.setOnClickListener(v -> {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                Toast.makeText(context, item.getName() + " removed from cart.", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        // Inner class for the ViewHolder
        static class CartViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, itemPrice;
            ImageView itemImage;
            Button removeButton;

            public CartViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.cartItemName);
                itemPrice = itemView.findViewById(R.id.cartItemPrice);
                itemImage = itemView.findViewById(R.id.cartItemImage);
                removeButton = itemView.findViewById(R.id.removeItemButton);
            }
        }
    }
}
