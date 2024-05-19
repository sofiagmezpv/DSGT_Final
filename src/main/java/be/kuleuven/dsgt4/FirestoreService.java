package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirestoreService {

    private final Firestore db;

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
