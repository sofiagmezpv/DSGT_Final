package be.kuleuven.dsgt4.models;

import be.kuleuven.dsgt4.services.FirestoreService;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Package extends Object {




    private Timestamp timestamp;
    private String description;
    private String name;
    private double price;
    private String id;
    private List<String> itemIds;
    private List<Item> items;
    private String reservationId;

    // Default constructor required for Jackson deserialization
    public Package() {
        // Initialize suppliers to an empty list
        //this.items = new ArrayList<>();

        timestamp = null;
    }

    // Constructor
    public Package(String id, String name, String description, List<String> itemIds,String reservationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.itemIds = itemIds;
        this.reservationId = reservationId;
        this.timestamp = null;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice() {
        double newPrice = 4;
        for (Item item : items) {
            newPrice += item.getPrice();
        }
        this.price = newPrice;
    }

    public void setReservationId(String id)
    {
            reservationId = id;
    }

    public String getReservationId(){
        return this.reservationId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<String> getItemNames() {
        return items.stream().map(Item::getName).collect(Collectors.toList());
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }
}
