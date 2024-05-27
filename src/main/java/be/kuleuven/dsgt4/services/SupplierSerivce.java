package be.kuleuven.dsgt4.services;


import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class SupplierSerivce {

    @Autowired
    WebClient.Builder webClientBuilder;
    ItemService itemLogic;



    @Autowired
    public SupplierSerivce(){
        this.itemLogic = new ItemService();
    }

    public List<Item> getAllItems()
    {

        return null;
    }


    public Mono<Item> getItemFromRest(String Itemid,Supplier sub){

        return this.webClientBuilder.baseUrl(sub.getBaseUrl()).build()
                .get().uri("/rest/drinks/{id}/{code}",Itemid,sub.getApiKey())
                .retrieve()
                .bodyToMono(Item.class);

    }

    public void reserveItem(Item item){
        Supplier sub = item.getSupplier();
        itemAvailable(item.getId(),1,sub);

    }

    public void buyItem(String id, int amount){

    }
    public Boolean itemAvailable(String id, int amount,Supplier sub) {
        Mono<Item> item = getItemFromRest(id,sub);
        System.out.println(item);
        return null;
    }


}
