package be.kuleuven.dsgt4.models;

import java.util.List;

public class Order {
    private String id;
    private List<String> packages;
    private String uid;
    private String ts;
    private double price;
    private String reservationId;

    // Default constructor (necessary for Firestore)
    public Order() {}

    // Parameterized constructor for convenience
    public Order(String id, List<String> packages, String uid, String ts, double price, String reservationId) {
        this.id = id;
        this.packages = packages;
        this.uid = uid;
        this.ts = ts;
        this.price = price;
        this.reservationId = reservationId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", packages=" + packages +
                ", uid='" + uid + '\'' +
                ", ts='" + ts + '\'' +
                ", price=" + price +
                '}';
    }
}
