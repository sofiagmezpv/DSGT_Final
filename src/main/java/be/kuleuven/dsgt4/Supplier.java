package be.kuleuven.dsgt4;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import netscape.javascript.JSObject;
import org.eclipse.jetty.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@RestController
public class Supplier {
        private final int apiKey;
        private static final String NAMESPACE_URI ="http://127.0.0.1:8100/rest";
        private String name;
        private List<Item> items;
        public String baseUrl;
        private final WebClient webClient;


        //TODO this can only be a data model class make new class for webclientbuilder

        @Autowired
        public Supplier(WebClient.Builder webClientBuilder,@Value("${supplier.apiKey}") int apiKey,String name, String baseUrl) {
                this.apiKey = apiKey;
                this.name = name;
                this.baseUrl = baseUrl;
                this.webClient = webClientBuilder.baseUrl(baseUrl).build();

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
