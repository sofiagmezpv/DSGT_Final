package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.cloud.Timestamp;

@Service
public class FirestoreService {

    private final Firestore db;

    public void addItemToUserCart(String username, Package pack) {

        System.out.println("In addItemToUserCart " + username);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("id", pack.getId());
        cartItem.put("name", pack.getName());
        cartItem.put("description", pack.getDescription());
        cartItem.put("price", pack.getPrice());
        cartItem.put("items", pack.getItems());

        Timestamp currentTimeStamp = Timestamp.now();
        cartItem.put("timestamp", currentTimeStamp);


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
        System.out.println("In getUserPackages with username: " + username);
        List<Package> userPackages = new ArrayList<>();

        try {
            CollectionReference cartRef = db.collection("users")
                    .document(username)
                    .collection("cart");

            ApiFuture<QuerySnapshot> query = cartRef.get();
            QuerySnapshot querySnapshot = query.get();

            System.out.println("Query snapshot size: " + querySnapshot.size());

            // Iterate over the documents in the query result
            for (QueryDocumentSnapshot document : querySnapshot) {
                Package pack = document.toObject(Package.class);
                userPackages.add(pack);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return userPackages;
    }

    public void removeItemFromUserCartByTimeStamp(String username, int itemId, Timestamp timestamp){
        System.out.println("Into removeItemFromUserCartByTimeStamp");
        Query query = db.collection("users").document(username).collection("cart")
                .whereEqualTo("id", itemId).whereEqualTo("timestamp", timestamp);

        // Execute the query asynchronously
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();

        try {
            // Get the results of the query
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();

            // Check if any documents were found
            if (!querySnapshot.isEmpty()) {
                // Assuming you want to delete the first document found
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                // Delete the document
                ApiFuture<WriteResult> deleteFuture = document.getReference().delete();

                try {
                    // Wait for the delete operation to complete
                    WriteResult deleteResult = deleteFuture.get();

                    if (deleteResult.getUpdateTime()!= null) {
                        System.out.println("Document deleted with ID: " + document.getId());
                    } else {
                        System.err.println("Unsuccessful delete operation: ");
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred during the delete operation: " + e.getMessage());
                }
            } else {
                System.out.println("No items found with ID: " + itemId);
            }
        } catch (Exception e) {
            System.err.println("An error occurred during the query operation: " + e.getMessage());
        }

    }

    public void removeItemFromUserCart(String username, int itemId) {
        System.out.println("Into RemoveItemFromUserCart");

        // Query to find the document(s) where id matches the itemId
        Query query = db.collection("users").document(username).collection("cart")
                .whereEqualTo("id", itemId);

        // Execute the query asynchronously
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();

        try {
            // Get the results of the query
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();

            // Check if any documents were found
            if (!querySnapshot.isEmpty()) {
                // Assuming you want to delete the first document found
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                // Delete the document
                ApiFuture<WriteResult> deleteFuture = document.getReference().delete();

                try {
                    // Wait for the delete operation to complete
                    WriteResult deleteResult = deleteFuture.get();

                    if (deleteResult.getUpdateTime()!= null) {
                        System.out.println("Document deleted with ID: " + document.getId());
                    } else {
                        System.err.println("Unsuccessful delete operation: ");
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred during the delete operation: " + e.getMessage());
                }
            } else {
                System.out.println("No items found with ID: " + itemId);
            }
        } catch (Exception e) {
            System.err.println("An error occurred during the query operation: " + e.getMessage());
        }
    }



    @Autowired
    public FirestoreService(Firestore db) {
        this.db = db;
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
