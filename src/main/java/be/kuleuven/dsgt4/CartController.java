package be.kuleuven.dsgt4;


import be.kuleuven.dsgt4.ShoppingCart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// Controller class to handle HTTP requests
@RestController
public class CartController {
    private ShoppingCart cart = new ShoppingCart();

    // Endpoint to add an item to the cart
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") int itemId) {
        // For example:
        // Use itemId to add the corresponding item to the cart
        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(new Supplier("Supplier A")); // Example supplier

        Item item = new Item("Summer Student Pack", "Ideal chilling vibes", 29.99, suppliers);
        cart.addItem(item);

        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("Item added to cart");
    }

    // Endpoint to remove an item from the cart
    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestBody Item item) {
        cart.removeItem(item);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Endpoint to proceed to payment
    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        double total = cart.calculateTotalPrice();
        // Implement logic to process payment
        return ResponseEntity.ok("Payment processed successfully. Total amount: " + total);
    }
}
