package be.kuleuven.dsgt4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class CartController {
    private ShoppingCart cart = new ShoppingCart();
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CartController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") int itemId) {
        Item item = getItemFromId(itemId);

        if(itemId == 1){
            Supplier subA = new Supplier(webClientBuilder, "Supplier A", "http://127.0.0.1:8100/rest"); // Example supplier
            Mono<String> test = subA.getDrinkById(1);

            item = new Item("Summer Student Pack", "Ideal summer vibes", 19.99, subA);
            test.subscribe(
                    value -> System.out.println(value),
                    error -> error.printStackTrace()
            );
        } else if (itemId == 2){
            Supplier subB = new Supplier(webClientBuilder, "Supplier B", "http://127.0.0.1:8100/rest"); // Example supplier
            item = new Item("Winter Student Pack", "Ideal winter vibes", 29.99, subB);
        }

        cart.addItem(item);
        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("Item added to cart");
    }

    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestBody Item item) {
        cart.removeItem(item);
        return ResponseEntity.ok("Item removed from cart");
    }

    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        double total = cart.calculateTotalPrice();
        return ResponseEntity.ok("Payment processed successfully. Total amount: " + total);
    }

    public Item getItemFromId(int id) {
        // Dummy Item until database is constructed
        Supplier subA = new Supplier(webClientBuilder, "subA", "http://127.0.0.1:8100/rest");
        return new Item("Cola", "Beverage", 12, subA);
    }

    public Mono<String> fetchdrinks() {
        return this.webClientBuilder.baseUrl("http://127.0.0.1:8100/rest").build().get()
                .uri("/drinks/1234")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getDrinks() {
        return fetchdrinks()
                .map(response -> "Processed Response: " + response)
                .doOnError(error -> System.err.println("Error fetching products: " + error.getMessage()))
                .doOnNext(result -> System.out.println("Received result: " + result))
                .doFinally(signalType -> System.out.println("Mono terminated with signal: " + signalType));
    }
}
