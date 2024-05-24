package be.kuleuven.dsgt4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Package extends Object {

    private String description;
    private String name;
    private double price;
    private int id;
    private List<Supplier> suppliers;

    // Default constructor required for Jackson deserialization
    public Package() {
        // Initialize suppliers to an empty list
        this.suppliers = new ArrayList<>();
    }

    // Constructor
    public Package(int id, String name, String description, double price, List<Supplier> suppliers) {
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

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<String> getSupplierNames() {
        return suppliers.stream().map(Supplier::getName).collect(Collectors.toList());
    }


}