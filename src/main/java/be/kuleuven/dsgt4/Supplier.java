package be.kuleuven.dsgt4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class Supplier {
        private WebClient webClient;
        private String name;
        private List<Item> items;
        private String baseUrl;

        @Autowired
        public Supplier(WebClient.Builder webClientBuilder, String name, String baseUrl) {
                this.name = name;
                this.baseUrl = baseUrl;
                this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        }

        public Mono<String> getDrinkById(int id) {
                System.out.println("trying to contact supplier");
                return webClient.get()
                        .uri("/drinksId/{id}/1234", id)
                        .retrieve()
                        .bodyToMono(String.class);
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
}
