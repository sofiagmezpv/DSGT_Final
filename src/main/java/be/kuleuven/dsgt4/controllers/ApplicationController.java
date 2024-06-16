package be.kuleuven.dsgt4.controllers;



import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.User;
import be.kuleuven.dsgt4.repositories.FirestoreRepository;
import be.kuleuven.dsgt4.services.PackageService;
import be.kuleuven.dsgt4.services.SupplierSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller class to handle HTTP requests
@RestController
public class ApplicationController {

    @Autowired
    private FirestoreRepository firestoreRepository;

    @Autowired
    private PackageService packageService;

    @Autowired
    private SupplierSerivce supplierSerivce;

    @Autowired
    public ApplicationController() {
    }


    // TODO: Sofia's changes
    @PostMapping("api/addUserCred")
    public ResponseEntity<String> addUserCred(@RequestParam("uid") String uid, @RequestParam("username") String username) {
        User user = WebSecurityConfig.getUser();
        System.out.println(user.getEmail());
        firestoreRepository.addUserToDb(uid, user.getEmail());

        System.out.println("Inside add user");
        return ResponseEntity.ok("User added to cart");
    }

    // Endpoint to add a package to the cart
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") String packageId, @RequestParam("uid") String uid) {
        System.out.println("***********Inside add item to cart***************");
        return supplierSerivce.addToCart(packageId, uid);
    }

    // Endpoint to get all packages of a user's cart
    @GetMapping("/user/packages/{uidString}")
    public ResponseEntity<?> getUserPackages(@PathVariable String uidString) {
        return supplierSerivce.getUserPackages(uidString);
    }

    // Endpoint to remove a package from the user's cart
    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestParam("id") String packageId, @RequestParam("uid") String uidString) {
        System.out.println("Cart Controller Remove with id:"+packageId);
        return supplierSerivce.removeFromCart(packageId,uidString);
    }

    // Endpoint to retrieve all the users of the webshop
    @GetMapping("/api/getAllCustomers")
    public ResponseEntity<?> getAllCustomers(Authentication authentication) {
        return supplierSerivce.getAllCustomers(authentication);
    }

    // Endpoint to retrieve all the past orders of the webshop
    @GetMapping("/api/getAllOrders")
    public ResponseEntity<?> getAllOrders(Authentication authentication) {
       return supplierSerivce.getAllOrders(authentication);
    }

    // Endpoint to buy and "pay" the user's cart
    @PostMapping("/buy_cart")
    public ResponseEntity<String> buyCart(@RequestParam("uid") String uidString) {
        System.out.println("*****user wants to buy******");
        return supplierSerivce.buyCart(uidString);
    }

    // Endpoint to retrieve all packages the webshop offers
    @GetMapping("/packages")
    public List<Package> getAllPackages() {
        return packageService.getAllPackages();
    }
}

