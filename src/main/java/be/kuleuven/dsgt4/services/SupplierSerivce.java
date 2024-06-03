package be.kuleuven.dsgt4.services;


import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;

import java.util.List;

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
                .get().uri("/drinksId/{id}/{code}",Itemid,1234)
                .retrieve()
                .bodyToMono(Item.class);

    }

    public void reservePack(Package pack){
        for(Item i: pack.getItems()){
            String reservationId = generateReservationId();
            firestoreService.addReservationIdTopackage(pack,reservationId);
            this.webClientBuilder.baseUrl(i.getSupplier().getBaseUrl()).build()
                    .post()
                    .uri("drinksId/{id}/reserve/{reservationId}/{packageId}/{code}",i.getId(),reservationId,pack.getId(),1234)
                    .retrieve()
                    .bodyToMono(Boolean.class);
        }
    }

    public void buyPack(Package pack){
        String reservationId = firestoreService.getReservationIdPackage(pack);
        for(Item i: pack.getItems()){
            System.out.println("buying item in pack"+i.getName());

            this.webClientBuilder.baseUrl(i.getSupplier().getBaseUrl()).build()
                    .get()
                    .uri("/drinksId/{reservationId}/buy/{code}",reservationId,1234)
                    .retrieve()
                    .bodyToMono(Boolean.class);
        }
    }
    public Mono<Boolean> itemAvailable(String id,Supplier sub) {
        System.out.println("baseurl supplier= " + sub.getBaseUrl()+ " itemid="+ id+" apikey= "+sub.getApiKey());
        //TODO check with mathilde
        return webClientBuilder.baseUrl(sub.getBaseUrl()).build()
                .post().uri("/drinksId/{id}/checkAvailability/{code}","Ndjb0HZE6s3uxnAryOqA",1234)
                .retrieve()
                .bodyToMono(Boolean.class);
    }


}
