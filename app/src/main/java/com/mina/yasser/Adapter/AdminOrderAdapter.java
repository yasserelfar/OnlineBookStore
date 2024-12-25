package com.mina.yasser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.AdminOrderActivity;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderDao orderDao;
    private CartDao cartDao;
    private ProductDao productDao;
    private Context context;

    public AdminOrderAdapter(List<Order> orderList, OrderDao orderDao, CartDao cartDao, ProductDao productDao, Context context) {
        this.orderList = (orderList != null) ? orderList : new ArrayList<>();

        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.context = context;
    }

    public AdminOrderAdapter(List<Order> orders, OrderDao orderDao, AdminOrderActivity adminOrderActivity) {
        this.orderList = (orderList != null) ? orderList : new ArrayList<>();

        this.orderDao = orderDao;
        this.context = adminOrderActivity;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Display basic order details
        holder.orderDetails.setText(
                "Order ID: " + order.getOrderId() +
                        "\nUser: " + order.getUserName() +
                        "\nPrice: $" + String.format("%.2f", order.getPrice()) +
                        "\nStatus: " + order.getStatus()
        );
        if ("Canceled".equals(order.getStatus())||"Shipping".equals(order.getStatus()))
        {
            holder.confirmButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        }
        holder.cancelButton.setOnClickListener(v -> {
            order.setStatus("Canceled");
            updateOrder(order,"Order Canceld");

            holder.cancelButton.setEnabled(false);
        });
        holder.confirmButton.setOnClickListener(v -> {

            order.setStatus("Confirmed");
            updateOrder(order,"Confirmed");
            holder.confirmButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        });
        // Fetch cart items for this order asynchronously using cartId
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch cart items associated with the order using the cartId
            List<Cart> cartItems = cartDao.getCartItemsByCartId(order.getCartId());

            StringBuilder bookDetails = new StringBuilder();
            if (cartItems != null && !cartItems.isEmpty()) {
                for (Cart cart : cartItems) {
                    // Fetch product details for each cart item
                    Product product = productDao.getProductByBarcodes(cart.getProductBarcode());
                    if (product != null) {
                        bookDetails.append("- ")
                                .append(product.getName())
                                .append(" (Qty: ")
                                .append(cart.getQuantity())
                                .append(")\n");
                    }
                }
            }

            // If no cart items found, display a default message
            String finalBookDetails = bookDetails.length() > 0 ? bookDetails.toString().trim() : "No books in this order.";

            // Update the UI with the final book details
            ((AppCompatActivity) context).runOnUiThread(() -> {
                holder.booksDetails.setText(finalBookDetails);
            });
        });

    }



    private void updateOrder(Order order, String message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            orderDao.updateOrder(order);
            ((AppCompatActivity) context).runOnUiThread(() -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetails, booksDetails;
        Button confirmButton, cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            booksDetails = itemView.findViewById(R.id.booksDetails);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
