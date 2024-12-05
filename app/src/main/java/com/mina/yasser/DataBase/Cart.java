package com.mina.yasser.DataBase;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "cart")

public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int cartId;
    private int userId;
    private int productId;
    private int quantity;

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getters and setters
}
