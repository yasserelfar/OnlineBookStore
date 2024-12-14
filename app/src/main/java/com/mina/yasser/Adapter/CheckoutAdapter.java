package com.mina.yasser.Adapter;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.R;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private final List<Cart> cartList;
    private final ProductDao productDao;
    private final Context context;

    public CheckoutAdapter(List<Cart> cartList, ProductDao productDao, CartDao cartDao, Context context) {
        this.cartList = cartList;
        this.productDao = productDao;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        // Fetch product details asynchronously using LiveData
        productDao.getProductByBarcode(cart.getProductBarcode()).observe((LifecycleOwner) context, product -> {
            if (product != null) {
                // Update UI on the main thread
                holder.productName.setText(product.getName());
                holder.productPrice.setText("Price: $" + product.getPrice());
                holder.productQuantity.setText("Quantity: " + cart.getQuantity());

                // Set the product image
                if (product.getImage() != null && product.getImage().length > 0) {
                    holder.productImage.setImageBitmap(BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length));
                } else {
                    // Set a default image if product image is missing
                    holder.productImage.setImageResource(R.drawable.cat6); // Replace with your default image
                }
            } else {
                // If product is not found
                Toast.makeText(context, "Product details not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.checkoutProductName);
            productPrice = itemView.findViewById(R.id.checkoutProductPrice);
            productQuantity = itemView.findViewById(R.id.checkoutProductQuantity);
            productImage = itemView.findViewById(R.id.checkoutProductImage);
        }
    }
}
