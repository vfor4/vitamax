package se.magnus.api.core.product;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
    Mono<Product> createProduct(@RequestBody Product body);

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    Mono<Product> getProduct(@PathVariable int productId,
                             @RequestParam(required = false, defaultValue = "0") int delay,
                             @RequestParam(required = false, defaultValue = "0") int faultPercent);

    @DeleteMapping(value = "/product/{productId}")
    Mono<Void> deleteProduct(@PathVariable int productId);
}
