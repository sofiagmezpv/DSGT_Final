package be.kuleuven.dsgt4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class Supplier {
        private int apiKey;
        private static final String NAMESPACE_URI ="http://127.0.0.1:8100/rest";
        private String name;
        private List<Item> items;
        public String baseUrl;


        @Autowired
        public Supplier(@Value("${supplier.apiKey}") int apiKey,String name, String baseUrl) {
                this.apiKey = apiKey;
                this.name = name;
                this.baseUrl = baseUrl;

        }

        // Getters and setters
        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public List<Item> getItems() {
                return items;
        }

        public void setItems(List<Item> items) {
                this.items = items;
        }

        public String getBaseUrl() {
                return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
        }
        public int getApiKey() {
                return apiKey;
        }
}
