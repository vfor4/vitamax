package se.magnus.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.microservices.core.product.persistence.ProductEntity;
import se.magnus.microservices.core.product.persistence.ProductRepository;
import se.magnus.util.http.ServiceUtil;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, final ProductRepository repository, final ProductMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Product> createProduct(final Product body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        return repository.save(mapper.apiToEntity(body))
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(mapper::entityToApi);
    }

    @Override
    public Mono<Product> getProduct(final int productId, final int delay, final int faultPercent) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .map(p -> mapper.entityToApi(this.throwIfBadLuck(p, faultPercent)))
                .delayElement(Duration.ofSeconds(delay))
                .map(this::setServiceAddress);
    }

    private final Random random = new Random();

    private ProductEntity throwIfBadLuck(final ProductEntity product, final int faultPercent) {
        if (faultPercent == 0) {
            return product;
        }
        final int randomNum = this.getRandomNumber(1, 100);
        if (faultPercent > randomNum ) {
            log.debug("ops, not that lucky hah?");
            throw new RuntimeException("something went wrong...");
        }
        log.info("you are luck bastard");
        return product;
    }

    private int getRandomNumber(final int min, final int max) {
        if ( min < max ) {
            throw new IllegalArgumentException("min value should not be less than max value");
        }
        return random.nextInt(((max - min) + 1) + min);
    }

    @Override
    public Mono<Void> deleteProduct(final int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        // idempotent
        log.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        return repository.findByProductId(productId).map(repository::delete).flatMap(Function.identity());
    }

    private Product setServiceAddress(Product e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }
}
