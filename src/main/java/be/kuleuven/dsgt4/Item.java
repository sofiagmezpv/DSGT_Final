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
    private String id;
    private Supplier supplier;
    private String supplierId;
    private String brand;

    // Default constructor
    public Item() {
        // Default constructor required for Jackson deserialization
    }
    // Constructor
    public Item(String id, String name, String description, double price, String supplierId, String brand) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.supplierId = supplierId;
        this.brand = brand;
        this.id = id;

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }    public Mono<Boolean> checkAvailablity() {
        System.out.println("Checking if item is available");

        return supplier.getItemById(this.id)
            .map(amount -> amount > 0);
        }

    public void reserveItem(){
        //TODO implement REST reserve request
        System.out.println("reserving item");
    }

}

