package be.kuleuven.dsgt4.services;


import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

import java.util.List;

import static org.eclipse.jetty.webapp.MetaDataComplete.True;

@RestController
public class SupplierSerivce {

    @Autowired
    private final WebClient.Builder webClientBuilder;
    private final ItemService itemLogic;
    private final FirestoreService firestoreService;



    @Autowired
    public SupplierSerivce(WebClient.Builder webClientBuilder, FirestoreService firestoreService){
        this.webClientBuilder = webClientBuilder;
        this.firestoreService = firestoreService;
        this.itemLogic = new ItemService();
    }

    public List<Item> getAllItems()
    {

        return null;
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
        firestoreService.addReservationIdToPackage(pack,reservationId,uid);
    }

    public void buyPack(Package pack,String uid){
        String reservationId = firestoreService.getReservationIdFromPackage(pack.getId(),uid);
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
        String reservationId = firestoreService.getReservationIdFromPackage(pack.getId(),uid);
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
        String reservationId = firestoreService.getReservationIdFromPackage(pack.getId(), uid);
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
        firestoreService.moveToOrder(uidString);
        firestoreService.removePackagesFromUser(uidString);
    }
}
