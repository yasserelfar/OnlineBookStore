package com.mina.yasser.ViewModel;

import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mina.yasser.AccountManagementActivity;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.DataBase.User;
import com.mina.yasser.DataBase.UserDao;
import com.mina.yasser.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutViewModel extends ViewModel {
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final int userId;

    private final MutableLiveData<Double> totalPriceLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Cart>> cartLiveData = new MutableLiveData<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private UserDao userDao;

    public CheckoutViewModel(CartDao cartDao, ProductDao productDao, OrderDao orderDao,UserDao userDao, int userId) {
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.userId = userId;
        this.userDao=userDao;
        // Load initial cart
        loadCartData();
    }

    // Expose cart data as LiveData
    public LiveData<List<Cart>> getCartLiveData() {
        return cartLiveData;
    }

    // Expose total price as LiveData
    public LiveData<Double> getTotalPriceLiveData() {
        return totalPriceLiveData;
    }
    // Load cart data from the database
    private void loadCartData() {
        executorService.execute(() -> {
            List<Cart> cartList = cartDao.getCartItemsByUserId(userId);
            cartLiveData.postValue(cartList);
            calculateTotalPrice(cartList);
        });
    }

    // Calculate total price
    private void calculateTotalPrice(List<Cart> cartList) {
        executorService.execute(() -> {
            double totalPrice = 0.0;
            for (Cart cart : cartList) {
                Product product = productDao.getProductByBarcodes(cart.getProductBarcode());
                if (product != null) {
                    totalPrice += product.getPrice() * cart.getQuantity();
                }
            }
            totalPriceLiveData.postValue(totalPrice);
        });
    }

    // Add order to the database
    public void addOrderToDatabase(List<Cart> cartList, double totalPrice) {
        executorService.execute(() -> {
            if (cartList != null && !cartList.isEmpty()) {
                try {
                    // Create a new Order and insert it into the database
                    Order newOrder = new Order();
                    newOrder.setUserId(userId);

                    newOrder.setUserName(userDao.getUserById(userId).getUsername()); // Replace with actual user name
                    newOrder.setPrice(totalPrice);
                    newOrder.setStatus("Pending");
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    newOrder.setDate(currentDate);
                    // Handle cartId properly. You may need to iterate over cartList for each item.
                    // Here we're assuming one cart per order for simplicity, but adjust for multiple items if necessary.
                    newOrder.setCartId(cartList.get(0).getCartId());

                    Log.d("CheckoutViewModel", "userId: " + userId);
                    orderDao.insertOrder(newOrder);  // Insert order without setting the orderId manually
                    Log.d("CheckoutViewModel", "orderId: " + newOrder.getOrderId());
                    Log.d("CheckoutViewModel", "cartId: " + newOrder.getCartId());
                    Log.d("CheckoutViewModel", "userIdOrder: " + newOrder.getUserId());

                    // Clear the cart after placing the order
                    cartDao.clearCartByUserId(userId);

                    // Refresh the cart data after clearing
                    loadCartData();

                    Log.d("CheckoutViewModel", "Order placed successfully!");

                } catch (Exception e) {
                    Log.e("CheckoutViewModel", "Error while adding order: " + e.getMessage());
                }
            } else {
                Log.e("CheckoutViewModel", "Cart is empty or null.");
            }
        });
    }



    // Refresh data (e.g., after an order is placed)
    public void refreshData() {
        loadCartData();
    }
}
