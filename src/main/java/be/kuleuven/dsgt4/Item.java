package be.kuleuven.dsgt4;

import java.util.List;

// Class representing an item
class Item {
    private String description;
    private String name;
    private double price;
    private int id;
    private List<Supplier> suppliers; // List of suppliers providing this item

    // Default constructor
    public Item() {
        // Default constructor required for Jackson deserialization
    }
    // Constructor
    public Item(int id, String name, String description, double price, List<Supplier> suppliers) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.suppliers = suppliers;
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
    public List<Supplier> getSuppliers() {
        return suppliers;
    }
}

