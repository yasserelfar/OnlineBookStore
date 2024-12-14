package com.mina.yasser.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    // Insert a product into the cart
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(Cart cart);

    // Update the quantity of a product in the cart
    @Query("UPDATE cart SET quantity = quantity + :quantity WHERE userId = :userId AND productBarcode = :productBarcode")
    void updateQuantity(int userId, String productBarcode, int quantity);

    // Get all products in the cart for a user (LiveData for observing changes)
    @Query("SELECT * FROM cart WHERE userId = :userId")
    LiveData<List<Cart>> getCartProducts(int userId);

    // Delete a specific cart entry
    @Delete
    void deleteCart(Cart cart);

    // Update an existing cart entry
    @Update
    void updateCart(Cart cart);

    // Get a cart item by product barcode for a specific user
    @Query("SELECT * FROM cart WHERE userId = :userId AND productBarcode = :productBarcode LIMIT 1")
    LiveData<Cart> getCartProduct(int userId, String productBarcode);

    // Clear the cart for a specific user
    @Query("DELETE FROM cart WHERE userId = :userId")
    void clearCartForUser(int userId);

    // Delete a cart item by userId and productBarcode
    @Query("DELETE FROM cart WHERE userId = :userId AND productBarcode = :productBarcode")
    void deleteCartItem(int userId, String productBarcode);

    // Check if a product exists in the cart
    @Query("SELECT * FROM cart WHERE userId = :userId AND productBarcode = :productBarcode LIMIT 1")
    LiveData<Cart> getCartItemByProduct(int userId, String productBarcode);
}

