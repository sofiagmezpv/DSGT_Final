package be.kuleuven.dsgt4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class Supplier {
        private static final String NAMESPACE_URI ="http://127.0.0.1:8100/rest";
        private String name;
        private List<Item> items;
        public String baseUrl;


        @Autowired
        public Supplier(String name, String baseUrl) {
                this.name = name;
                this.baseUrl = baseUrl;

        }

        @PayloadRoot()



        // Constructor, getters, setters...


}
