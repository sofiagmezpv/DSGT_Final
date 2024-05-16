package be.kuleuven.dsgt4;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Item> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }
// Remove item from the cart
    public void removeItem(Item item) {
        items.remove(item);
    }

    // Calculate total price of items in the cart
    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Item item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    // Pay for the items in the cart
    public void pay() {
        // Logic to process payment (e.g., connect to payment gateway)
        System.out.println("Payment successful! Total amount: " + calculateTotalPrice());
        // After payment, you may want to clear the cart
        items.clear();
    }

    // Getters and setters...
}

