package be.kuleuven.dsgt4.services;


import be.kuleuven.dsgt4.models.*;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.repositories.FirestoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;

import java.util.List;

@RestController
public class SupplierSerivce {

    @Autowired
    private final WebClient.Builder webClientBuilder;

    @Autowired
    private FirestoreRepository firestoreRepository;

    @Autowired
    private PackageService packageService;

    @Autowired
    public SupplierSerivce(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    public String generateReservationId(){
        int length = 10;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(length );
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }


    public Mono<Item> getItemFromRest(String Itemid,Supplier sub){
        System.out.println("baseurl supplier= " + sub.getBaseUrl()+ " itemid="+ Itemid+" apikey= "+sub.getApiKey());
        return webClientBuilder.baseUrl(sub.getBaseUrl()).build()
                .get().uri("/itemId/{id}/{code}",Itemid,1234)
                .retrieve()
                .bodyToMono(Item.class);

    }

    public void reservePack(Package pack,String uid){
        System.out.println("***RESERVING PACK****");
        String reservationId = generateReservationId();
        for(Item i: pack.getItems()){
            try {
                this.webClientBuilder.baseUrl(i.getSupplier().getBaseUrl()).build()
                        .post()
                        .uri("/itemId/{id}/reserve/{reservationId}/{code}", i.getId(), reservationId, 1234)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(error -> {
                            // Handle the error, e.g., log it, or take specific action
                            System.err.println("Error reserving item: " + i.getId() + " - " + error.getMessage());
                        })
                        .block();
            } catch (Exception e) {
                System.err.println("Failed to reserve item " + i.getId() + ": " + e.getMessage());
                return; //make sure only to to add reservationIdTOpackage when all items are oke
            }
        }
        firestoreRepository.addReservationIdToPackage(pack,reservationId,uid);
    }

    public void buyPack(Package pack,String uid){
        String reservationId = firestoreRepository.getReservationIdFromPackage(pack.getId(),uid);
        for (Item i : pack.getItems()) {
            try {
                this.webClientBuilder.baseUrl(i.getSupplier().getBaseUrl()).build()
                        .post()
                        .uri("/buy/{reservationId}/{code}", reservationId, 1234)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(error -> {
                            // Handle the error, e.g., log it, or take specific action
                            System.err.println("Error buying item: " + i.getId() + " - " + error.getMessage());
                        })
                        .block();
            } catch (Exception e) {
                System.err.println("Failed to buy item " + i.getId() + ": " + e.getMessage());
                return;
            }
        }

    }

    public Mono<Boolean> itemAvailable(String id,Supplier sub) {
        System.out.println("baseurl supplier= " + sub.getBaseUrl()+ " itemid="+ id+" apikey= "+sub.getApiKey());

        return webClientBuilder.baseUrl(sub.getBaseUrl()).build()
                .post()
                .uri("/itemId/{id}/checkAvailability/{code}",id,1234)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Boolean checkreservation(Package pack,String uid) {
        String reservationId = firestoreRepository.getReservationIdFromPackage(pack.getId(),uid);
        //String reservationId = pack.getReservationId();
        System.out.println("pack reservation id: "+reservationId);
        List<Mono<Boolean>> checks = pack.getItems().stream()
                .map(it -> webClientBuilder.baseUrl(it.getSupplier().getBaseUrl()).build()
                        .post()
                        .uri("/itemId/{id}/checkReservation/{reservationId}/{code}", it.getId(), reservationId, 1234)
                        .retrieve()
                        .bodyToMono(Boolean.class))
                .toList();

        return Flux.concat(checks)
                .all(result -> result)
                .block(); // Block and get the result
    }

    public void releasePack(Package pack, String uid) {
        String reservationId = firestoreRepository.getReservationIdFromPackage(pack.getId(), uid);
        System.out.println("**RELEASING PACKAGES***");
        for (Item it : pack.getItems()) {
            try {
                webClientBuilder.baseUrl(it.getSupplier().getBaseUrl()).build()
                        .post()
                        .uri("/releaseReservation/{reservationId}/{code}", reservationId, 1234)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block(); // Blocking call to ensure the request completes
            } catch (Exception e) {
                System.err.println("Failed to release reservation for item " + it.getId() + ": " + e.getMessage());

            }
        }
    }

    public void movePacktoOrder(String uidString) {
        firestoreRepository.moveToOrder(uidString);
        firestoreRepository.removePackagesFromUser(uidString);
    }

    public ResponseEntity<String> addToCart(String packageId, String uid) {
        Package pack = packageService.getPackageFromId(packageId);
        if (pack == null) {
            return ResponseEntity.status(404).body("Package not found");
        }

        for (Item it : pack.getItems()) {
            Mono<Boolean> av =this.itemAvailable(it.getId(), it.getSupplier());
            Boolean available = av.block();
            System.out.println("item available:" + available);
            if (!available) {
                //to do make visible on screen
                return ResponseEntity.ok("Items can't be added to cart");
            }

        }
        firestoreRepository.addItemToUserCart(uid, pack);
        this.reservePack(pack, uid);

        return ResponseEntity.ok("Item added to cart");
    }

     public ResponseEntity<?> getUserPackages(String uidString) {
         List<Package> userPackages = firestoreRepository.getUserPackages(uidString);
         if (userPackages == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user packages.");
         }
         System.out.println("Size Package List" + userPackages.size());
         return ResponseEntity.ok(userPackages);
     }

     public ResponseEntity<String> removeFromCart(String packageId, String uidString) {
         Package pack = packageService.getPackageFromId(packageId);
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (!authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
         }

         this.releasePack(pack,uidString);
         firestoreRepository.removeItemFromUserCart(uidString, packageId);
         return ResponseEntity.ok("Item removed from cart");
     }

     public ResponseEntity<?> getAllCustomers(Authentication authentication) {
         if (authentication == null || authentication.getAuthorities().stream()
                 .noneMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
         }
         List<User> allUsers = firestoreRepository.getAllCustomers();

         if (allUsers == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user packages.");
         }
         if (allUsers.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User packages not found for username: ");
         }
         return ResponseEntity.ok(allUsers);
     }

     public ResponseEntity<?> getAllOrders(Authentication authentication) {
         if (authentication == null || authentication.getAuthorities().stream()
                 .noneMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
         }
         System.out.println("Inside api/getAllOrders");
         List<Order> allPurchasedOrders = firestoreRepository.getAllOrders();

         if (allPurchasedOrders == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all purchased carts.");
         }
         if (allPurchasedOrders.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No purchased found");
         }
         return ResponseEntity.ok(allPurchasedOrders);
     }


     public ResponseEntity<String> buyCart(String uidString) {
         List<Package> packages = firestoreRepository.getUserPackages(uidString);
         System.out.println(uidString + " has packages:" + packages);
         for(Package pack: packages) {
             Boolean status = this.checkreservation(pack,uidString);
             System.out.println("package available: " + status);
             if (!status) {
                 return ResponseEntity.ok("Coudn't buy pack");
             }
         }
         for(Package pack:packages){
             this.buyPack(pack,uidString);
         }

         this.movePacktoOrder(uidString);
         return ResponseEntity.ok("Item all bought");
     }

}
