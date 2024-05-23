package be.kuleuven.dsgt4;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Package> packs;

    public ShoppingCart() {
        this.packs = new ArrayList<>();
    }

    public void addItem(Package pack) {
        packs.add(pack);
    }
// Remove item from the cart
//    public void removeItem(int itemPos) {
//        //call method that ghets item with itemId
//        packs.remove(getItemAtPosition(itemPos));
//    }

//    public Item getItemAtPosition(int idPos) {
//        // Check if the idPos is within the bounds of the array
//        if (idPos >= 0 && idPos < packs.size()) {
//            return packs.get(idPos);
//        } else {
//            // Handle the case where idPos is out of bounds
//            return null; // Or throw an exception, depending on your requirements
//        }
//    }

    // Calculate total price of items in the cart
    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Package pack : packs) {
            totalPrice += pack.getPrice();
        }
        return totalPrice;
    }

    // Pay for the items in the cart
    public void pay() {
        // Logic to process payment (e.g., connect to payment gateway)
        System.out.println("Payment successful! Total amount: " + calculateTotalPrice());
        // After payment, you may want to clear the cart
        packs.clear();
    }

    public List<Package> getItems() {
        return packs;
    }
}

