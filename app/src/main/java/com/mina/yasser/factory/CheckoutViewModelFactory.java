package com.mina.yasser.factory;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.DataBase.UserDao;
import com.mina.yasser.ViewModel.CheckoutViewModel;

public class CheckoutViewModelFactory implements ViewModelProvider.Factory {
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final int userId;
    private UserDao userDao;
    public CheckoutViewModelFactory(CartDao cartDao, ProductDao productDao, OrderDao orderDao, UserDao userDao, int userId) {
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.userId = userId;
        this.userDao=userDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckoutViewModel.class)) {
            return (T) new CheckoutViewModel(cartDao, productDao, orderDao,userDao, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
