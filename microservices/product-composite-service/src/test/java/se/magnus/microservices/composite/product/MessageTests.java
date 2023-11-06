package se.magnus.microservices.composite.product;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.product.ProductAggregate;
import se.magnus.api.composite.product.RecommendationSummary;
import se.magnus.api.composite.product.ReviewSummary;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import({TestChannelBinderConfiguration.class})
public class MessageTests {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private OutputDestination target;

    @BeforeEach
    void setUp() {
        purgeMessages("products");
        purgeMessages("recommendations");
        purgeMessages("reviews");
    }

    @Test
    void testCreateProduct() {
        ProductAggregate p = new ProductAggregate(1, "first", 99, null, null, null);
        postAndVerifyResponse(HttpStatus.ACCEPTED, p);
        List<String> products = getMessages("products");
        Event event = new Event(CREATE, 1, new Product(1, "first", 99, null));
        MatcherAssert.assertThat(products.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(event)));

        List<String> recommendations = getMessages("recommendations");
        List<String> reviews = getMessages("reviews");
        Assertions.assertEquals(0, recommendations.size());
        Assertions.assertEquals(0, reviews.size());
    }

    @Test
    void testCreateProduct2() {
        ProductAggregate p = new ProductAggregate(1, "first", 99,
                Collections.singletonList(new RecommendationSummary(2, "vu", 2, "it's good book")),
                Collections.singletonList(new ReviewSummary(3, "lan", "tester", "it's bug")), null);
        postAndVerifyResponse(HttpStatus.ACCEPTED, p);
        List<String> products = getMessages("products");
        Event eventP = new Event(CREATE, 1, new Product(1, "first", 99, null));
        MatcherAssert.assertThat(products.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventP)));

        List<String> recommendations  = getMessages("recommendations");
        Event eventR = new Event(CREATE, 1, new Recommendation(1, 2, "vu", 2, "it's good book", null));
        MatcherAssert.assertThat(recommendations.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventR)));

        List<String> reviews = getMessages("reviews");
        Event eventRev = new Event(CREATE, 1, new Review(1,3, "lan", "tester", "it's bug", null));
        MatcherAssert.assertThat(reviews.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventRev)));
    }

    @Test
    void testDeleteProduct() {
        deleteAndVerifyResponse(HttpStatus.ACCEPTED, 1);
        List<String> products = getMessages("products");
        Event eventP = new Event(DELETE, 1, null);
        MatcherAssert.assertThat(products.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventP)));

        List<String> recommendations  = getMessages("recommendations");
        MatcherAssert.assertThat(recommendations.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventP)));

        List<String> reviews = getMessages("reviews");
        MatcherAssert.assertThat(reviews.get(0), CoreMatchers.is(IsSameEvent.sameEventExceptCreatedAt(eventP)));
    }

    void purgeMessages(String bindingName) {
        getMessages(bindingName);
    }

    List<String> getMessages(String bindingName) {
        List<String> messages = new ArrayList<>();
        boolean isMoreMessages = true;
        while (isMoreMessages) {
            Message<byte[]> m = getMessage(bindingName);
            if (m == null) isMoreMessages = false;
            else messages.add(new String(m.getPayload()));
        }
        return messages;
    }

    Message<byte[]> getMessage(String bindingName) {
        try {
            return target.receive(0, bindingName);
        } catch (NullPointerException e) {
            return null;
        }
    }

    void postAndVerifyResponse(HttpStatus status, ProductAggregate product) {
        webClient.post().uri("/product-composite")
                .body(Mono.just(product), ProductAggregate.class)
                .exchange().expectStatus().isEqualTo(status);
    }

    void deleteAndVerifyResponse(HttpStatus status, int productId) {
        webClient.delete().uri("/product-composite/" + productId)
                .exchange().expectStatus().isEqualTo(status);
    }

}
