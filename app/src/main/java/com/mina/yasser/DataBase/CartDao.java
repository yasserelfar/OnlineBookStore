package com.mina.yasser.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface CartDao {

    // Insert a new product into the cart
    @Insert
    void addToCart(Cart cart);

    // Update the quantity of a product in the cart
    @Update
    void updateCart(Cart cart);

    // Remove a product from the cart
    @Delete
    void removeFromCart(Cart cart);

    // Get all items in the cart for a specific user
    @Query("SELECT * FROM cart WHERE userId = :userId")
    List<Cart> getCartItems(int userId);

    // Clear all items in the cart for a specific user
    @Query("DELETE FROM cart WHERE userId = :userId")
    void clearCart(int userId);
}
