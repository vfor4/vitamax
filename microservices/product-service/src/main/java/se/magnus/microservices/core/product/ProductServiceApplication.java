package se.magnus.microservices.core.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("se.magnus")
@SpringBootApplication
public class ProductServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);

        String mongoDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        String mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        log.info("Connected to MongoDb: " + mongoDbHost + ":" + mongoDbPort);
    }

//    @Autowired
//    ReactiveMongoOperations mongoTemplate;
//
//    @EventListener(ContextRefreshedEvent.class)
//    public void initIndicesAfterStartup() {
//
//        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
//        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
//
//        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(ProductEntity.class);
//        resolver.resolveIndexFor(ProductEntity.class).forEach(e -> indexOps.ensureIndex(e).block());
//    }
}
