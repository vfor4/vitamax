package se.magnus.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
	@RestController
	static class CustomErrorController implements ErrorController {

		private static final String ERROR_MAPPING = "/error";

		@RequestMapping(ERROR_MAPPING)
		public ResponseEntity<Void> error() {
			return ResponseEntity.notFound().build();
		}
	}
}
