package be.kuleuven.dsgt4;


// Class representing an item
class Item {
    private String description;
    private String name;
    private double price;
    private int id;
    private Supplier supplier;

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


}

