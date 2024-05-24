package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FirestoreService {

    private final Firestore db;
    private final ResourceLoader loader;

    public void addItemToUserCart(String username, Package pack) {

        System.out.println("In addItemToUserCart" + username);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("id", pack.getId());
        cartItem.put("name", pack.getName());
        cartItem.put("description", pack.getDescription());
        cartItem.put("price", pack.getPrice());
//        cartItem.put("suppliers", pack.getSuppliers());

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
            // Query the "cart" collection for items associated with the user
            CollectionReference cartRef = db.collection("users")
                    .document(username)
                    .collection("cart");

            ApiFuture<QuerySnapshot> query = cartRef.get();
            QuerySnapshot querySnapshot = query.get();

            System.out.println("Query snapshot size: " + querySnapshot.size());

            // Iterate over the documents in the query result
            for (QueryDocumentSnapshot document : querySnapshot) {
                System.out.println("Processing document: " + document.getId());

                        // Get the package details from the cart item
                Package pack = document.toObject(Package.class);
                userPackages.add(pack);

                System.out.println("Package added: " + pack.toString());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return userPackages;
    }

    public void removeItemFromUserCart(String username, int itemId) {
        System.out.println("Into RemoveItemFromUserCart");

        // Query to find the document(s) where id matches the itemId
        Query query = db.collection("users").document(username).collection("cart")
                .whereEqualTo("id", itemId); // Corrected to use whereEqualTo instead of whereIn

        // Execute the query asynchronously
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();

        try {
            // Get the results of the query
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();

            // Check if any documents were found
            if (!querySnapshot.isEmpty()) {
                // Delete each document found
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
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
                }
            } else {
                System.out.println("No items found with ID: " + itemId);
            }
        } catch (Exception e) {
            System.err.println("An error occurred during the query operation: " + e.getMessage());
        }
    }


    @Autowired
    public FirestoreService(Firestore db, ResourceLoader loader) {
        this.db = db;
        this.loader = loader;
    }

    @PostConstruct
    void initializeDB() {
        Resource resource = loader.getResource("classpath:db_data.json");
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = resource.getInputStream()) {
            // Read JSON into a Map
            Map<String, Object> firestoreData = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});

            WriteBatch batch = db.batch();

            // Iterate over each entry in the Map
            for (Map.Entry<String, Object> entry : firestoreData.entrySet()) {
                String collectionName = entry.getKey();
                List<Map<String, Object>> documents = (List<Map<String, Object>>) entry.getValue();

                for (Map<String, Object> document : documents) {
                    String documentId = (String) document.get("id");
                    batch.set(db.collection(collectionName).document(documentId), document);
                }
            }

            // Commit the batch
            List<WriteResult> results = batch.commit().get();
            for (WriteResult result : results) {
                System.out.println("Update time: " + result.getUpdateTime());
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
