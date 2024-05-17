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
    public void removeItem(int itemPos) {
        //call method that ghets item with itemId
        items.remove(getItemAtPosition(itemPos));
    }

    public Item getItemAtPosition(int idPos) {
        // Check if the idPos is within the bounds of the array
        if (idPos >= 0 && idPos < items.size()) {
            return items.get(idPos);
        } else {
            // Handle the case where idPos is out of bounds
            return null; // Or throw an exception, depending on your requirements
        }
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

    public List<Item> getItems() {
        return items;
    }
}

