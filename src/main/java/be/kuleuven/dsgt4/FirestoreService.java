package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private final Firestore db;

    public void addItemToUserCart(String username, Package pack) {

        System.out.println("In addItemToUserCart" + username);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("itemId", pack.getId());
        cartItem.put("name", pack.getName());
        cartItem.put("description", pack.getDescription());
        cartItem.put("price", pack.getPrice());
        cartItem.put("suppliers", pack.getSupplierNames());
        cartItem.put("username", username); // Add the username field

        ApiFuture<DocumentReference> future = db.collection("users")
                .document(username)
                .collection("cart")
                .add(cartItem);

        try {
            DocumentReference docRef = future.get();
            System.out.println("Item added to cart: " + docRef.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Package> getUserPackages(String username) {
        System.out.println("In getUserPackages" + username);
        List<Package> userPackages = new ArrayList<>();

        try {
            // Query the "cart" collection for items associated with the user
            CollectionReference cartRef = db.collection("users")
                    .document(username)
                    .collection("cart");

            ApiFuture<QuerySnapshot> query = cartRef.whereEqualTo("username", username).get();
            QuerySnapshot querySnapshot = query.get();

            // Iterate over the documents in the query result
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Get the package details from the cart item
                Package pack = document.toObject(Package.class);
                userPackages.add(pack);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return userPackages;
    }


    @Autowired
    public FirestoreService() {
        Dsgt4Application app = new Dsgt4Application();
        this.db = app.db();
    }

    @PostConstruct
    void initializeDB() {
        // add suppliers
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", "caffeine shop");
        docData.put("apiKey", 1234);
        docData.put("baseUrl", "http://127.0.0.1:8100/rest");
        ApiFuture<WriteResult> future = db.collection("supplier").document("SCImO9FqmkJELue1zi8f").set(docData);


        //add items
        Map<String, Object> docData2 = new HashMap<>();
        docData2.put("name", "Nalu Original");
        docData2.put("description", "An amazing new taste of Nalu Drinks with less calories and a boost of energy.");
        docData2.put("price", 1.09);
        docData2.put("supplier", "SCImO9FqmkJELue1zi8f");
        ApiFuture<DocumentReference> future2 = db.collection("item").add(docData2);



    }

}
