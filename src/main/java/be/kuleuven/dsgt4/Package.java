package be.kuleuven.dsgt4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.cloud.Timestamp;





public class Package extends Object {

    private String description;
    private String name;
    private double price;
    private int id;
    private Timestamp timestamp;
    private List<Item> items = new ArrayList<>();;

    // Default constructor required for Jackson deserialization
    public Package() {

    }

    // Constructor
    public Package(int id, String name, String description, double price, List<Item> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.items = items;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Timestamp getTimestamp(){return timestamp;}

    public List<Item> getItems() {
        return items;
    }



}
