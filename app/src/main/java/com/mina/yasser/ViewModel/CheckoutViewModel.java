package com.mina.yasser.ViewModel;

import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mina.yasser.AccountManagementActivity;
import com.mina.yasser.CheckoutActivity;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.OrderDetailDao;
import com.mina.yasser.DataBase.OrderItem;
import com.mina.yasser.DataBase.OrderItemDao;
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

    private OrderItemDao orderItemDao;
    private CategoryDao categoryDao;
    private CategoryDao categoryViewModel;

    public CheckoutViewModel(CartDao cartDao, ProductDao productDao, OrderDao orderDao, UserDao userDao, OrderItemDao orderItemDao, CategoryDao categoryDao,CategoryDao categoryViewModel, int userId) {
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.userId = userId;
        this.userDao=userDao;
        this.orderItemDao=orderItemDao;
        this.categoryDao=categoryDao;
        this.categoryViewModel=categoryViewModel;
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
        if (cartList == null || cartList.isEmpty()) {
            Log.e("CheckoutViewModel", "Cart list is null or empty. Cannot place order.");
            return;
        }

        Log.d("carts", "Cart size is " + cartList.size());
        executorService.execute(() -> {
            try {
                // Retrieve cart ID and validate cart existence
                int cartId = cartList.get(0).getCartId();
                Cart cart = cartDao.getCartItemsByCartId(cartId);
                if (cart == null) {
                    Log.e("CheckoutViewModel", "Cart with ID " + cartId + " does not exist.");
                    return;
                }

                // Create and insert the order
                Order newOrder = new Order();
                newOrder.setUserId(userId);
                newOrder.setUserName(userDao.getUserById(userId).getUsername());
                newOrder.setPrice(totalPrice);
                newOrder.setStatus("Pending");
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                newOrder.setDate(currentDate);
                newOrder.setCartId(cartId);

                // Insert order and retrieve generated ID
                Log.d("CheckoutViewModel", "Order created with ID: " + newOrder.getOrderId());
                orderDao.insertOrder(newOrder);
// إدخال الطلب أولاً
                long insertedOrderId = orderDao.insertOrder(newOrder);
                if (insertedOrderId == 0) {
                    Log.e("CheckoutViewModel", "Failed to insert Order into the database.");
                    return;
                }
                newOrder.setOrderId((int) insertedOrderId); // التأكد من تعيين الـ orderId بعد الإدخال

                // Insert OrderItems for each Cart item
                for (Cart cartItem : cartList) {
                    // Check product existence
                    Product product = productDao.getProductByBarcodes(cartItem.getProductBarcode());
                    if (product == null) {
                        Log.e("CheckoutViewModel", "Product not found: " + cartItem.getProductBarcode());
                        continue;
                    }

                    Log.d("CheckoutViewModel", "Product found: " + product.getName()+" "+product.getBarcode());

                    // Check category existence
                    Category category = categoryDao.getCategoryByIdSync(product.getCategoryId());
                    if (category == null) {
                        Log.e("CheckoutViewModel", "Category with ID " + product.getCategoryId() + " does not exist.");
                        continue;
                    }

                    Log.d("CheckoutViewModel", "Category found: " + category.getName()+" "+category.getId() );

                    // Create and insert OrderItem
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(newOrder.getOrderId());
                    orderItem.setProductBarcode(cartItem.getProductBarcode());
                    orderItem.setQuantity(cartItem.getQuantity());

                    // Insert OrderItem
                     orderItemDao.insert(orderItem);
                    Log.d("CheckoutViewModel", "OrderItem added with ID: " + orderItem.getId());
                }

                // Clear the cart after order completion
                cartDao.clearCartByUserId(userId);

                // Reload cart data
                loadCartData();

                Log.d("CheckoutViewModel", "Order and OrderItems placed successfully!");
            } catch (Exception e) {
                Log.e("CheckoutViewModel", "Error while adding order: " + e.getMessage(), e);
            }
        });
    }









    // Refresh data (e.g., after an order is placed)
    public void refreshData() {
        loadCartData();
    }
}
