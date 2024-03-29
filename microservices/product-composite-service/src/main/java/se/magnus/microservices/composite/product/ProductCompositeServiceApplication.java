package se.magnus.microservices.composite.product;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@ComponentScan("se.magnus")
@SpringBootApplication
public class ProductCompositeServiceApplication {
    @Value("${api.common.version}") String apiVersion;
    @Value("${api.common.title}")String apiTitle;
    @Value("${api.common.description}")String apiDescription;
    @Value("${api.common.termsOfService}")String apiTermsOfService;
    @Value("${api.common.license}")String apiLicense;
    @Value("${api.common.licenseUrl}")String apiLicenseUrl;
    @Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
    @Value("${api.common.externalDocUrl}")String apiExternalDocUrl;
    @Value("${api.common.contact.name}")String apiContactName;
    @Value("${api.common.contact.url}")String apiContactUrl;
    @Value("${api.common.contact.email}")String apiContactEmail;

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;


    @Bean
    RestTemplate getRestemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenAPI getOpenApiDocumentation() {
        return new OpenAPI()
                .info(new Info().title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name(apiContactName)
                                .url(apiContactUrl)
                                .email(apiContactEmail))
                        .termsOfService(apiTermsOfService)
                        .license(new License()
                                .name(apiLicense)
                                .url(apiLicenseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description(apiExternalDocDesc)
                        .url(apiExternalDocUrl));
    }

    @Bean
    Scheduler publishEventScheduler(@Value("${app.threadPoolSize}") int poolSize, @Value("${app.taskQueueSize}") int taskQueue) {
        return Schedulers.newBoundedElastic(poolSize, taskQueue, "publish-event-pool");
    }

    @Bean
    WebClient webClient(final WebClient.Builder builder) {
        return builder.filter(lbFunction).build();
    }

    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }
}
