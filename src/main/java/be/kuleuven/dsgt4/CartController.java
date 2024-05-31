package be.kuleuven.dsgt4;


import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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


    @PostMapping("/addUserCred")
        public ResponseEntity<String> addUserCred(@RequestParam("uid") String uid, @RequestParam("username") String username) {

        firestoreService.addUserToDb(uid, username);

        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("User added to cart");
    }

    // Endpoint to add a package to the cart
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") String itemId, @RequestParam("uid") String uid) {

        Package pack = packageService.getPackageFromId(itemId);
        if (pack == null) {
            return ResponseEntity.status(404).body("Package not found");
        }

        firestoreService.addItemToUserCart(uid, pack);

        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("Item added to cart");
    }

//    @PostMapping ("/api/adduser")
//    public ResponseEntity<String>  adduser() throws InterruptedException, ExecutionException {
//        System.out.println("Inside api adduser");
//        var user = WebSecurityConfig.getUser();
//
//        firestoreService.addUserToDb(user);
//
//        System.out.println("Inside api adduser");
//        return ResponseEntity.ok("user added to cart");
//    }

    @GetMapping("/user/packages/{uidString}")
    public ResponseEntity<?> getUserPackages(@PathVariable String uidString) {
        List<Package> userPackages = firestoreService.getUserPackages(uidString);

        if (userPackages == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user packages.");
        }

        if (userPackages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User packages not found for UID: " + uidString);
        }

        return ResponseEntity.ok(userPackages);
    }

    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestParam("id") String itemId, @RequestParam("uid") String uidString) {
        System.out.println("Cart Controller Remove");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        // Optionally, update the Firestore database to reflect the change in the cart
        firestoreService.removeItemFromUserCart(uidString, itemId);

        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/api/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
    List<User> allUsers = firestoreService.getAllCustomers();

    if (allUsers == null) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user packages.");
    }

    if (allUsers.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User packages not found for username: ");
    }

    return ResponseEntity.ok(allUsers);
    }


    @GetMapping("/api/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        System.out.println("Inside api/getAllOrders");
        List<Cart> purchasedCarts = firestoreService.getAllOrders();

        if (purchasedCarts == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all purchased carts.");
        }

        if (purchasedCarts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No purchased found");
        }

        return ResponseEntity.ok(purchasedCarts);
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
