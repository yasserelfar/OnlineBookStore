package com.mina.yasser.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.R;

import java.util.List;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Cart> cartList;
    private final ProductDao productDao;
    private final CartDao cartDao;
    private final Context context;

    public CartAdapter(List<Cart> cartList, ProductDao productDao, CartDao cartDao, Context context) {
        this.cartList = cartList;
        this.productDao = productDao;
        this.cartDao = cartDao;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
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

        // Increase quantity
        holder.increaseQuantity.setOnClickListener(v -> {
            cart.setQuantity(cart.getQuantity() + 1);
            updateCart(cart, holder);
        });

        // Decrease quantity
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
                updateCart(cart, holder);
            } else {
                Toast.makeText(context, "Minimum quantity is 1.", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove from cart
        holder.removeFromCart.setOnClickListener(v -> {
            removeCartItem(cart, position, holder);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void updateCart(Cart cart, CartViewHolder holder) {
        Executors.newSingleThreadExecutor().execute(() -> {
            cartDao.updateCart(cart);
            // Ensure UI updates are done on the main thread
            holder.itemView.post(() -> holder.productQuantity.setText("Quantity: " + cart.getQuantity()));
        });
    }

    private void removeCartItem(Cart cart, int position, RecyclerView.ViewHolder holder) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Delete the cart item from the database
            cartDao.deleteCart(cart);

            // Remove the item from the list
            cartList.remove(position);

            // Notify the adapter to update the RecyclerView
            holder.itemView.post(() -> {
                // Notify that the item was removed
                notifyItemRemoved(position);
                // Notify that the remaining items need to be updated
                notifyItemRangeChanged(position, cartList.size());
                Toast.makeText(context, "Product removed from cart.", Toast.LENGTH_SHORT).show();
            });
        });
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        Button increaseQuantity, decreaseQuantity, removeFromCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.cartProductName);
            productPrice = itemView.findViewById(R.id.cartProductPrice);
            productQuantity = itemView.findViewById(R.id.cartProductQuantity);
            productImage = itemView.findViewById(R.id.cartProductImage);
            increaseQuantity = itemView.findViewById(R.id.cartIncreaseQuantity);
            decreaseQuantity = itemView.findViewById(R.id.cartDecreaseQuantity);
            removeFromCart = itemView.findViewById(R.id.cartRemoveButton);
        }
    }
}
