package be.kuleuven.dsgt4;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

// Class representing an item
class Item {
    private String description;
    private String name;
    private double price;
    private int id;
    private Supplier supplier;
    //for json data
    private int kcal;
    private double volume;
    private String brand;
    private int amount;



    @Autowired
    WebClient.Builder webClientBuilder;
    // Default constructor
    public Item() {
        // Default constructor required for Jackson deserialization
    }
    // Constructor
    public Item(int id, String name, String description, double price, Supplier supplier) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.supplier = supplier;
        this.id = id;

    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getId() {return id;}

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Supplier getSuppliers() {
        return supplier;
    }

    public Mono<Boolean> checkAvailablity() {
        System.out.println("Checking if item is available");

        return supplier.getItemById(this.id)
            .map(amount -> amount > 0);
        }

    public void reserveItem(){
        //TODO implement REST reserve request
        System.out.println("reserving item");
    }

}

