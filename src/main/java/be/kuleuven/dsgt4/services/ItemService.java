package be.kuleuven.dsgt4.services;

import be.kuleuven.dsgt4.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

public class ItemService {
    @Autowired
    WebClient.Builder webclient;

    ItemService(){

    }

    public Item getItemById(String ItemId){
        return null;
    }
}
