package be.kuleuven.dsgt4;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
public class BuyController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public BuyController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/buttonPage") // Endpoint to display the button page
    public String buttonPage() {
        return "buttonPage"; // Return the view with the button
    }

    @PostMapping("trigger-rest-call")
    public Mono<String> triggerRestCall() {
        // Make the REST call using WebClient
        WebClient webClient = webClientBuilder.build(); // Create WebClient instance from the builder
        return webClient.get()
                .uri("your-rest-endpoint-url") // Replace with your actual REST endpoint URL
                .retrieve()
                .bodyToMono(String.class); // Assuming you expect a string response
    }
}
