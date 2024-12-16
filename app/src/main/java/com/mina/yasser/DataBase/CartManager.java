package com.mina.yasser.DataBase;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;  // Singleton instance
    private List<CartItem> cartItems;     // List of cart items

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    // Singleton pattern to get the instance of CartManager
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();

        }
        return instance;
    }

    // Add a product to the cart
    public void addToCart(Product product) {
        for (CartItem item : cartItems) {
            // Check if the product is already in the cart using its ID (barcode)
            if (item.getProductId().equals(product.getBarcode())) {
                // If the product is already in the cart, increase its quantity
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        // If the product is not in the cart, add it as a new CartItem
        cartItems.add(new CartItem(
                product.getBarcode(),  // Use the product's barcode as ID
                product.getName(),     // Use the product's name
                product.getPrice(),    // Use the product's price
                1                      // Initial quantity is 1
        ));
    }

    // Get all cart items
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    // Clear all items from the cart
    public void clearCart() {
        cartItems.clear();
    }
}
