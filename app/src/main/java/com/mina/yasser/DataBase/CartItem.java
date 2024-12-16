package com.mina.yasser.DataBase;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private String category;


    // Constructor
    public CartItem(String productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Method to calculate total price for this cart item
    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return productName + " (Qty: " + quantity + ") - $" + price + " each";
    }
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
