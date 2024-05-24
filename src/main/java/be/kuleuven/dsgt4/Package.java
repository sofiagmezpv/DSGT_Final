package be.kuleuven.dsgt4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Package extends Object {

    private String description;
    private String name;
    private double price;
    private int id;
    private List<Item> items;

    // Default constructor required for Jackson deserialization
    public Package() {
        // Initialize suppliers to an empty list
        this.items = new ArrayList<>();
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

    public List<Item> getItems() {
        return items;
    }

    public List<String> getItemNames() {
        return items.stream().map(Item::getName).collect(Collectors.toList());
    }


}
