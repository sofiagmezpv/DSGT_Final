package be.kuleuven.dsgt4.repositories;

import be.kuleuven.dsgt4.models.Item;
import be.kuleuven.dsgt4.models.Order;
import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.models.Supplier;
import be.kuleuven.dsgt4.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.security.SecureRandom;
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


import java.util.ArrayList;

@Service
public class FirestoreRepository {

    private final Firestore db;
    private final ResourceLoader loader;

    @Autowired
    public FirestoreRepository(Firestore db, ResourceLoader loader) {
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


    public List<User> getAllCustomers() {
        System.out.println("In getAllCustomers ");
        List<User> usersAll = new ArrayList<>();
        try {
            CollectionReference usersRef = db.collection("users");
            ApiFuture<QuerySnapshot> query = usersRef.get();
            QuerySnapshot querySnapshot = query.get();
            System.out.println("Query snapshot size: " + querySnapshot.size());

            for (QueryDocumentSnapshot document : querySnapshot) {
                String email = document.getString("email");
                String role = document.getString("role");
                User user = new User(email, role);
                usersAll.add(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return usersAll;
    }

    public void addUserToDb(String uid, String username) {
        System.out.println("in addUserToDb Firestore service method");
        Map<String, Object> userItem = new HashMap<>();
        userItem.put("uid", uid);
        userItem.put("email", username);
        userItem.put("role", "");

        DocumentReference docRef = db.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> getDoc = docRef.get();

        try {
            DocumentSnapshot snapshot = getDoc.get();
            if (!snapshot.exists()) {
                // Document does not exist, proceed to create it
                ApiFuture<WriteResult> future = docRef.set(userItem);
                WriteResult result = future.get();
                System.out.println("Document added successfully");
            } else {
                System.out.println("User already exists in the database.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addItemToUserCart(String uid, Package pack) {
        System.out.println("In addItemToUserCart: " + uid);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("id", pack.getId());
        cartItem.put("name", pack.getName());
        cartItem.put("description", pack.getDescription());
        cartItem.put("price", pack.getPrice());
        cartItem.put("items", pack.getItems());
        cartItem.put("reservationId",pack.getReservationId());
        Timestamp currentTimeStamp = Timestamp.now();
        cartItem.put("timestamp", currentTimeStamp);

        ApiFuture<DocumentReference> future = db.collection("carts")
                .document(uid)
                .collection("packages")
                .add(cartItem);

        try {
            DocumentReference docRef = future.get();
            System.out.println("Item added to cart: " + docRef.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Package> getUserPackages(String uidString) {
        List<Package> userPackages = new ArrayList<>();
        try {
            CollectionReference cartRef = db.collection("carts")
                    .document(uidString)
                    .collection("packages");

            ApiFuture<QuerySnapshot> query = cartRef.get();
            QuerySnapshot querySnapshot = query.get();
            if(querySnapshot.size()!=0) {
                System.out.println("Query snapshot size: " + querySnapshot.size());

                // Iterate over the documents in the query result
                for (QueryDocumentSnapshot document : querySnapshot) {
                    boolean isFull = false;
                    System.out.println("package Id: " + document.getId());
                    Package pack = document.toObject(Package.class);
                    userPackages.add(pack);
                }
            } else {
                System.out.println("****NO packages found ***");
                return userPackages;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return userPackages;
    }

    public void removeItemFromUserCart(String uidString, String itemId) {
        System.out.println("Into RemoveItemFromUserCart");

        // Query to find the document(s) where id matches the itemId
        Query query = db.collection("carts").document(uidString).collection("packages")
                .whereEqualTo("id", itemId);

        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();
        try {
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            if (!querySnapshot.isEmpty()) {
                // Assuming you want to delete the first document found
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
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

    public List<Order> getAllOrders() {
        System.out.println("In getAllOrders");
        List<Order> allOrders = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection("orders").get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Order order = document.toObject(Order.class);
                allOrders.add(order);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return allOrders;
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
        System.out.println("items in pack:"+items);

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

    public void addReservationIdToPackage(Package pack, String reservationId, String uid) {
        CollectionReference packagesRef = db.collection("carts").document(uid).collection("packages");
        System.out.println("uid:"+uid);
        // Create a query to find the document with the matching package ID
        Query query = packagesRef.whereEqualTo("id", pack.getId());
        try {
            // Execute the query
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            System.out.println("documents:"+documents);
            if (!documents.isEmpty()) {
                // Document found, update the reservation ID
                QueryDocumentSnapshot document = documents.get(0); // Assuming there's only one match
                DocumentReference packageDocRef = document.getReference();

                Map<String, Object> updates = new HashMap<>();
                updates.put("reservationId", reservationId);

                ApiFuture<WriteResult> updateFuture = packageDocRef.update(updates);
                WriteResult result = updateFuture.get();
                System.out.println("Reservation ID added to package. Update time: " + result.getUpdateTime());
            } else {
                // Document not found
                System.err.println("Package document not found:" + pack.getId()+".");
            }
        } catch (InterruptedException e) {
            System.err.println("Update operation was interrupted");
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Update operation failed: " + e.getMessage());
        }
    }

    public String getReservationIdFromPackage(String id, String uid) {
        System.out.println("Retrieving reservation ID from package with id: " + id);
        CollectionReference packagesRef = db.collection("carts").document(uid).collection("packages");
        Query query = packagesRef.whereEqualTo("id", id);

        try {
            // Execute the query
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            if (documents.size() > 0) {
                // Document found, retrieve the reservation ID
                DocumentSnapshot document = documents.get(0); // Assuming there's only one match
                if (document.contains("reservationId")) {
                    String reservationId = document.getString("reservationId");
                    System.out.println("Reservation ID retrieved: " + reservationId);
                    return reservationId;
                } else {
                    System.err.println("Reservation ID not found in the package document");
                }
            } else {
                // Document not found
                System.err.println("Package document not found: " + id);
            }
        } catch (InterruptedException e) {
            System.err.println("Retrieve operation was interrupted");
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Retrieve operation failed: " + e.getMessage());
        }
        return null;
    }


    public void moveToOrder(String uid) {
        System.out.println("****trying to move to order list****");
        CollectionReference packagesRef = db.collection("carts").document(uid).collection("packages");
        CollectionReference ordersRef = db.collection("orders");
        List<Package> packages = getUserPackages(uid);
        try {
            if (!packages.isEmpty()) {
                Timestamp timestamp = getUserPackages(uid).get(0).getTimestamp();
                String reservationId = getReservationIdFromPackage(getUserPackages(uid).get(0).getId(),uid);
                // List to store package IDs
                List<String> packageIds = new ArrayList<>();
                // Loop through documents and collect package IDs
                double totalprice = 0;
                for (Package pack : packages) {
                    String packageId = pack.getId(); // Assuming id is the document ID
                    packageIds.add(packageId);
                    totalprice = totalprice +pack.getPrice();
                }
                System.out.println("****printing all the packageIds*****:"+packageIds);
                // Create new Order object
                Order order = new Order();
                order.setId(generatedRandomOrder()); // Set a unique order ID, or generate dynamically
                order.setPackages(packageIds);
                order.setUid(uid);
                order.setTs(timestamp.toString()); // Set timestamp or use a date/time library
                order.setPrice(totalprice); // Set the price accordingly
                order.setReservationId(reservationId); // Set reservation ID if applicable

                // Save the Order object to Firestore
                DocumentReference newOrderRef = ordersRef.document(order.getId());
                ApiFuture<WriteResult> writeFuture = newOrderRef.set(order);
                WriteResult writeResult = writeFuture.get();
                System.out.println("Order created in orders collection. Write time: " + writeResult.getUpdateTime());


            } else {
                // Documents not found
                System.err.println("not all packages moved");
            }
        } catch (InterruptedException e) {
            System.err.println("Operation was interrupted");
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    public void removePackagesFromUser(String uid) {
        System.out.println("removing packages");
        Query query = db.collection("carts").document(uid).collection("packages");
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();
        try {
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            if (!querySnapshot.isEmpty()) {
                List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
                // Start from the second document (index 1)
                for (int i = 0; i < documents.size(); i++) {
                    DocumentSnapshot document = documents.get(i);
                    ApiFuture<WriteResult> deleteFuture = document.getReference().delete();
                    try {
                        WriteResult deleteResult = deleteFuture.get();
                        if (deleteResult.getUpdateTime() != null) {
                            System.out.println("Document deleted with ID: " + document.getId());
                        } else {
                            System.err.println("Unsuccessful delete operation for document ID: " + document.getId());
                        }
                    } catch (Exception e) {
                        System.err.println("An error occurred during the delete operation for document ID: " + document.getId() + " - " + e.getMessage());
                    }
                }
            } else {
                System.out.println("No packages found");
            }
        } catch (Exception e) {
            System.err.println("An error occurred during the query operation: " + e.getMessage());
        }
    }

    public String generatedRandomOrder(){
        int length = 10;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(length );
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
