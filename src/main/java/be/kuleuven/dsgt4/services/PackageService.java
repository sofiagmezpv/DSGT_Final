package be.kuleuven.dsgt4.services;

import java.util.List;
import java.util.Map;

import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PackageService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public PackageService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    private FirestoreService firestoreService;

    public Package getPackageFromId(String id) {
        System.out.println("trying to find packs");
        Map<String, Package> packages = firestoreService.fetchPackages();
        return packages.get(id);
    }

    public Package getPackageFromId(int id) {
        // Example data, replace this with actual data fetching logic
        // Create Supplier instances using the injected WebClient.Builder
        Supplier supplierA = new Supplier(1,"1234","suplierA","http://127.0.0.1:8100/rest");
        Supplier supplierB = new Supplier(2,"1234", "Supplier B","http://127.0.0.1:8200/rest");

        // Creating items
        Item item1 = new Item("1","item1", "Item A", 100.0,"1","nanu");
        Item item2 = new Item("2","item1", "Item A", 100.0,"2","nanu");
        Item item3 = new Item("3","item1", "Item A", 100.0,"1","nanu");





        if (id == 1) {
            return new Package("1", "Summer Student Pack", "Ideal summer vibes", List.of(item1,item2),"");
        } else if (id == 2) {
            return new Package("2", "Winter Student Pack", "Ideal winter vibes", List.of(item2, item3),"");
        } else {
            // Default or null return, adjust as needed for your application
            return null;
        }

    }
}
