package be.kuleuven.dsgt4;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

// Controller class to handle HTTP requests
@RestController
public class CartController {
    private final WebClient webClient;

    @Autowired
    private FirestoreService firestoreService;
    @Autowired
    private PackageService packageService;

    public CartController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8100/rest").build();
    }

    // Endpoint to add a package to the cart
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") String itemId, @RequestParam("username") String username) {

        Package pack = packageService.getPackageFromId(itemId);
        if (pack == null) {
            return ResponseEntity.status(404).body("Package not found");
        }

        firestoreService.addItemToUserCart(username, pack);

        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("Item added to cart");
    }


    @GetMapping("/user/packages/{username}")
    public ResponseEntity<?> getUserPackages(@PathVariable String username) {
        System.out.println("Inside CartController user/packages/username");
        List<Package> userPackages = firestoreService.getUserPackages(username);

        if (userPackages == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user packages.");
        }

        if (userPackages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User packages not found for username: " + username);
        }

        return ResponseEntity.ok(userPackages);
    }

    @GetMapping("/api/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        List<Cart> purchasedCarts = firestoreService.getAllOrders();

        if (purchasedCarts == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all purchased carts.");
        }

        if (purchasedCarts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No purchased found");
        }

        return ResponseEntity.ok(purchasedCarts);
    }

    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestParam("id") String itemId, @RequestParam("username") String username) {
        System.out.println("Cart Controller Remove");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        // Optionally, update the Firestore database to reflect the change in the cart
        firestoreService.removeItemFromUserCart(username, itemId);

        return ResponseEntity.ok("Item removed from cart");
    }




    // Endpoint to remove an item from the cart
//    @PostMapping("/remove_from_cart")
//    public ResponseEntity<String> removeFromCart(@RequestParam("id") int itemId)  {
//        cart.removeItem(itemId);
//        return ResponseEntity.ok("Item removed from cart");
//    }

    // Endpoint to proceed to payment
    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        // Implement logic to process payment
        return ResponseEntity.ok("Payment processed successfully. Total amount: " /*+ total*/);
    }


    //get item from database with the id
//    public Item getItemFromId(int id){
//        //dummie Item until database is constructed
//        Supplier subA = new Supplier("subbA","http://127.0.0.1:8100/rest");
//        Item item = new Item(id,"cola","beverage",12,subA);
//        return null;
//    }


    public Mono<String> fetchdrinks() {
        return this.webClient.get()
                .uri("/drinks/1234")
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> getDrinks() {
        return fetchdrinks()
                .map(response -> {
                    // Process the response here, for example, convert it to JSON
                    return "Processed Response: " + response;
                })
                .doOnError(error -> {
                    // Handle any errors that occur during the processing
                    System.err.println("Error fetching products: " + error.getMessage());
                })
                .doOnNext(result -> {
                    // Log or perform any actions on the result
                    System.out.println("Received result: " + result);
                })
                .doFinally(signalType -> {
                    // Perform cleanup or logging when the Mono terminates (success, error, or cancellation)
                    System.out.println("Mono terminated with signal: " + signalType);
                });
    }

}
