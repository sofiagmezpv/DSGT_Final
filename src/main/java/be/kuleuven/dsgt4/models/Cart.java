package be.kuleuven.dsgt4.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    // A list to hold the packages
    private List<String> packages;

    // Constructor to initialize the Cart
    public Cart() {
        packages = new ArrayList<>();
    }

    // Method to add a package to the cart
    public void addPackage(String packageId) {
        packages.add(packageId);
    }

    // Method to remove a package from the cart
    public void removePackage(String packageId) {
        packages.remove(packageId);
    }

    // Method to get all packages in the cart
    public List<String> getPackages() {
        return new ArrayList<>(packages);
    }

    // Method to get the number of packages in the cart
    public int getPackageCount() {
        return packages.size();
    }

}