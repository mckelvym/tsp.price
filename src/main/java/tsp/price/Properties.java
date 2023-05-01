package tsp.price;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties class. See application.properties
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Data
@Getter
@ToString
@EnableConfigurationProperties
@ConfigurationProperties("tsp")
@Configuration
public class Properties {
    /**
     * The remote service URL
     *
     * @since Apr 24, 2023
     */
    private String serviceUrl;
}
