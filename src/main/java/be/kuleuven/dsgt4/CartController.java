package be.kuleuven.dsgt4;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

// Controller class to handle HTTP requests
@RestController
public class CartController {
    private ShoppingCart cart = new ShoppingCart();
    private final WebClient webClient;

    public CartController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8100/rest").build();
    }

    // Endpoint to add an item to the cartµ
    @Autowired
    @PostMapping("/add_to_cart")
    public ResponseEntity<String> addToCart(@RequestParam("id") int itemId) {
        Item item = getItemFromId(itemId);
        var test = getDrinks();

        System.out.println(test.subscribe(
                value -> System.out.println(value),
                error -> error.printStackTrace()
                ));
        if(itemId == 1){
            // Use itemId to add the corresponding item to the cart
            Supplier subA = new Supplier("Supplier A","http://127.0.0.1:8100/rest"); // Example supplier
            item = new Item(itemId,"Summer Student Pack", "Ideal summer vibes", 19.99, subA);
        }else if (itemId == 2){
            Supplier subB = new Supplier("Supplier A","http://127.0.0.1:8100/rest"); // Example supplier
            item = new Item(itemId,"Winter Student Pack", "Ideal winter vibes", 29.99, subB);
        }

        cart.addItem(item);

        System.out.println("Inside add item to cart");
        return ResponseEntity.ok("Item added to cart");
    }

    // Endpoint to remove an item from the cart
    @PostMapping("/remove_from_cart")
    public ResponseEntity<String> removeFromCart(@RequestParam("id") int itemId)  {
        cart.removeItem(itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Endpoint to proceed to payment
    @PostMapping("/pay")
    public ResponseEntity<String> pay() {
        double total = cart.calculateTotalPrice();
        // Implement logic to process payment
        return ResponseEntity.ok("Payment processed successfully. Total amount: " + total);
    }


    //get item from database with the id
    public Item getItemFromId(int id){
        //dummie Item until database is constructed
        Supplier subA = new Supplier("subbA","http://127.0.0.1:8100/rest");
        Item item = new Item(id,"cola","beverage",12,subA);
        return null;
    }


    public Mono<String> fetchdrinks() {
        return this.webClient.get()
                .uri("/drinks/1234")
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> getDrinks() {
        return fetchdrinks()
                .map(response -> {
                    // Process the response here, for example, convert it to JSON
                    return "Processed Response: " + response;
                })
                .doOnError(error -> {
                    // Handle any errors that occur during the processing
                    System.err.println("Error fetching products: " + error.getMessage());
                })
                .doOnNext(result -> {
                    // Log or perform any actions on the result
                    System.out.println("Received result: " + result);
                })
                .doFinally(signalType -> {
                    // Perform cleanup or logging when the Mono terminates (success, error, or cancellation)
                    System.out.println("Mono terminated with signal: " + signalType);
                });
    }

}
