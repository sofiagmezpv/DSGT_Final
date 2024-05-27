package be.kuleuven.dsgt4.models;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class Supplier {
        private String id;
        private String apiKey;
        private static final String NAMESPACE_URI ="http://127.0.0.1:8100/rest";
        private String name;
        private List<Item> items;
        public String baseUrl;


        public Supplier(String id,String apiKey, String name, String baseUrl) {
                this.apiKey = apiKey;
                this.name = name;
                this.baseUrl = baseUrl;
                this.id = id;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getApiKey() {
                return apiKey;
        }

        public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
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

/*
        public Mono<Integer> getItemById(int id) {

                System.out.print("Trying to contact supplier for id ");
                System.out.println(id);
                return this.webClient.get()
                        .uri("/drinksId/{id}/1234", id)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> {
                                try {
                                        // Parse JSON string to Map
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        Map<String, ?> jsonData = objectMapper.readValue(response, Map.class);

                                        // Extract amount from Map
                                        return (Integer) jsonData.get("amount");
                                } catch (JsonProcessingException e) {
                                        // Handle parsing exception
                                        e.printStackTrace();
                                        return null; // Or throw a custom exception if needed
                                }
                        });
        }
*/
        public void setItems(List<Item> items) {
                this.items = items;
        }

        public String getBaseUrl() {
                return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
        }
//todo impelment logic
        public  Mono<Integer> getItemById(String id) {
                return null;
        }
}
