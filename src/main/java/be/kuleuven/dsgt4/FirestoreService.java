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
import org.springframework.core.io.Resource;

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
        cartItem.put("items", pack.getItems());


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
                boolean isFull = false;
                System.out.println("Cart Id: " + document.getId());
                Package pack = document.toObject(Package.class);
//                for(Item item : pack.getItems() ){
//                    Supplier supplier = item.getSupplier();
//                    int id = item.getId();
//                    System.out.print("Item id: " + id);;
//                    System.out.println("Item supplier: " + supplier.getName());
//                    // make REST Supplier request to supplier to check the availability of each item in pack
////                    if(not okay then delete the package)
//
//                }
                userPackages.add(pack);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return userPackages;
    }

    public void removeItemFromUserCart(String username, String itemId) {
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

    public List<Supplier> fetchSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> documents = db.collection("supplier").get().get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                suppliers.add(document.toObject(Supplier.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<Item> fetchItems() {
        List<Item> items = new ArrayList<>();
        List<Supplier> suppliers = this.fetchSuppliers();
        Map<String, Supplier> supplierMap = new HashMap<>();

        for (Supplier supplier : suppliers) {
            supplierMap.put(supplier.getId(), supplier);
        }

        try {
            List<QueryDocumentSnapshot> documents = db.collection("item").get().get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Item item = document.toObject(Item.class);
                Supplier supplier = supplierMap.get(item.getSupplierId());
                item.setSupplier(supplier);
                items.add(item);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return items;
    }

    public Map<String,Package> fetchPackages() {
        Map<String,Package> packages = new HashMap<>();
        List<Item> items = this.fetchItems();
        Map<String, Item> itemsMap = new HashMap<>();

        for (Item item : items) {
            itemsMap.put(item.getId(), item);
        }

        try {
            List<QueryDocumentSnapshot> documents = db.collection("package").get().get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                Package pack = document.toObject(Package.class);
                List<Item> packageItems = new ArrayList<>();
                List<String> itemIds = (List<String>) document.get("itemIds");

                for (String itemId : itemIds) {
                    if (itemsMap.containsKey(itemId)) {
                        packageItems.add(itemsMap.get(itemId));
                    }
                }
                pack.setItems(packageItems);
                pack.updatePrice(); //set price based on the items in the pack
                packages.put(pack.getId(),pack);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return packages;
    }


}
