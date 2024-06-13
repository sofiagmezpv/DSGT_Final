package be.kuleuven.dsgt4.controllers;



import be.kuleuven.dsgt4.models.Order;
import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.User;
import be.kuleuven.dsgt4.services.FirestoreService;
import be.kuleuven.dsgt4.services.PackageService;
import be.kuleuven.dsgt4.services.SupplierSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

// Controller class to handle HTTP requests
@RestController
public class CartController {

    @Autowired
    private FirestoreService firestoreService;
    //private WebClient.Builder webClientBuilder;
    @Autowired
    private PackageService packageService;

    SupplierSerivce supplierLogic;

    @Autowired
    public CartController(WebClient.Builder webClientBuilder, FirestoreService firestoreService) {
        this.supplierLogic = new SupplierSerivce(webClientBuilder, firestoreService);
    }


    @PostMapping("api/addUserCred")
    public ResponseEntity<String> addUserCred(@RequestParam("uid") String uid, @RequestParam("username") String username) {
        User user = WebSecurityConfig.getUser();
        System.out.println(user.getEmail());
        firestoreService.addUserToDb(uid, user.getEmail());

        System.out.println("Inside add user");
        return ResponseEntity.ok("User added to cart");
    }

    // Endpoint to add a package to the cart
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") String packageId, @RequestParam("uid") String uid) {
        System.out.println("***********Inside add item to cart***************");
        Package pack = packageService.getPackageFromId(packageId);
        if (pack == null) {
            return ResponseEntity.status(404).body("Package not found");
        }

        for (Item it : pack.getItems()) {
            Mono<Boolean> av = this.supplierLogic.itemAvailable(it.getId(), it.getSupplier());
            Boolean available = av.block();
            System.out.println("item available:"+available);
            if (!available) {
                //to do make visible on screen
                return ResponseEntity.ok("Items can't be added to cart");
            }

        }
        firestoreService.addItemToUserCart(uid, pack);
        supplierLogic.reservePack(pack,uid);



        return ResponseEntity.ok("Item added to cart");
    }


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
    public ResponseEntity<String> removeFromCart(@RequestParam("id") String packageId, @RequestParam("uid") String uidString) {
        System.out.println("Cart Controller Remove with id:"+packageId);
        Package pack = packageService.getPackageFromId(packageId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        //supplierLogic.releasePack(firestoreService.getUserPackages());
        // Optionally, update the Firestore database to reflect the change in the cart


        supplierLogic.releasePack(pack,uidString);
        firestoreService.removeItemFromUserCart(uidString, packageId);
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
        List<Order> allPurchasedOrders = firestoreService.getAllOrders();

        if (allPurchasedOrders == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all purchased carts.");
        }

        if (allPurchasedOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No purchased found");
        }

        return ResponseEntity.ok(allPurchasedOrders);
    }


    //TODO TEST this function
        //test buying
        //test removing
    @PostMapping("/buy_cart")
    public ResponseEntity<String> buyCart(@RequestParam("uid") String uidString) {
        System.out.println("*****user wants to buy******");
        List<Package> packages = firestoreService.getUserPackages(uidString);
        System.out.println(uidString + " has  packages:" + packages);
        for(Package pack: packages) {
            Boolean status = supplierLogic.checkreservation(pack,uidString);
            System.out.println("all packages available: " + status);
            if (status)
            {
                supplierLogic.buyPack(pack,uidString);
                return ResponseEntity.ok("Item all bought");
            } else {
                for(Item it:pack.getItems()){
                    removeFromCart(it.getId(),uidString);
                }
                supplierLogic.releasePack(pack,uidString);
            }
        }
        return null;
    }


    // Endpoint to proceed to payment
    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        // Implement logic to process payment
        System.out.println("trying to pay");
        return ResponseEntity.ok("Payment processed successfully. Total amount: " /*+ total*/);
    }
}


    //get item from database with the id
//    public Item getItemFromId(int id){
//        //dummie Item until database is constructed
//        Supplier subA = new Supplier("subbA","http://127.0.0.1:8100/rest");
//        Item item = new Item(id,"cola","beverage",12,subA);
//        return null;
//    }

/*
    public Mono<String> fetchdrinks() {
        return this.webClient.baseUrl("http://127.0.0.1:8100/rest").build()
                .get()
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
*/
