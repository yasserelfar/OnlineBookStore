package com.mina.yasser.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mina.yasser.DataBase.CartItem;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.mina.yasser.R;


public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {
    private List<CartItem> cartItems;

    public CheckoutAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);//can't resolve symbol item_checkout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.titleTextView.setText(cartItem.getProductName());  // Use getProductName() instead of getTitle
        holder.quantityTextView.setText("Quantity: " + cartItem.getQuantity()+"  ");
        holder.priceTextView.setText("Price: $" + cartItem.getPrice());
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, quantityTextView, priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bookTitle);//can't resolve symbol bookTitle
            quantityTextView = itemView.findViewById(R.id.bookQuantity);//can't resolve symbol bookQuantity
            priceTextView = itemView.findViewById(R.id.bookPrice);//can't resolve symbol bookPrice
        }
    }
}
