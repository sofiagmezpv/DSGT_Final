package be.kuleuven.dsgt4.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Package extends Object {

    private String description;
    private String name;
    private double price;
    private String id;
    private List<String> itemIds;
    private List<Item> items;

    // Default constructor required for Jackson deserialization
    public Package() {
        // Initialize suppliers to an empty list
        this.items = new ArrayList<>();
    }

    // Constructor
    public Package(String id, String name, String description, List<Item> ItemIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.itemIds = itemIds;
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
        double newPrice = 0;
        for (Item item : items) {
            newPrice += item.getPrice();
        }
        this.price = newPrice;
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
}
