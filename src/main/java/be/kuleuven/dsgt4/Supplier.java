package be.kuleuven.dsgt4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Supplier {
        private int apiKey;
        private String name;
        private String baseUrl;

        public Supplier(@Value("${supplier.apiKey}") int apiKey, String baseUrl, String name) {
                this.apiKey = apiKey;
                this.baseUrl = baseUrl;
                this.name = name;
        }

        public int getApiKey() {
                return apiKey;
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
