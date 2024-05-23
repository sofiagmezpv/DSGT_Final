package be.kuleuven.dsgt4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class Supplier {
        private int apiKey;
        private String name;
        private String baseUrl;
//        private List<Item> items; // Assuming you have an Item class defined

//        // Default constructor required for Firestore deserialization
//        public Supplier() {
//        }

        public Supplier(int apiKey, String baseUrl, String name) {
                this.apiKey = apiKey;
                this.baseUrl = baseUrl;
                this.name = name;
//                this.items = items;
        }

        public String getName() {
                return name;
        }

        public String getBaseUrl() {
                return baseUrl;
        }

//        public List<Item> getItems() {
//                return items;
//        }
}
