package tsp.price;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import tsp.price.xfer.Request;

/**
 * Downloads TSP share price information
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@SpringBootApplication
public class Application {
    /**
     * @param args
     * @since Apr 24, 2023
     */
    @SuppressWarnings("resource")
    public static void main(final String[] args) {
        System.setProperty("spring.main.allow-bean-definition-overriding",
                "true");
        System.setProperty("spring.main.web-application-type", "NONE");

        System.exit(SpringApplication
                .exit(SpringApplication.run(Application.class, args)));
    }

    /**
     * @return new {@link Request}
     * @since Apr 24, 2023
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Request request() {
        return new Request();
    }

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder p_Builder) {
        return p_Builder.build();
    }
}
