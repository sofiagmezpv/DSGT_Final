package be.kuleuven.dsgt4;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

    public Package getPackageFromId(int id) {
        // Example data, replace this with actual data fetching logic
        Supplier supplierA = new Supplier(1, "http://127.0.0.1:8100/rest", "Supplier A" );
        Supplier supplierB = new Supplier(2, "http://127.0.0.1:8200/rest", "Supplier B" );

        // Creating items
        Item item1 = new Item(1, "Item A", "Description for Item A", 100.0, supplierA);
        Item item2 = new Item(2, "Item B", "Description for Item B", 200.0, supplierA);
        Item item3 = new Item(3, "Item C", "Description for Item C", 300.0, supplierB);

        if (id == 1) {
            return new Package(1, "Summer Student Pack", "Ideal summer vibes", 19.99, List.of(item1, item2));
        } else if (id == 2){
            return new Package(2, "Winter Student Pack", "Ideal winter vibes", 29.99, List.of(item2, item3));
        } else {
            // Default or null return, adjust as needed for your application
            return null;
        }
    }
}
