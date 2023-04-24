package tsp.price;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * Properties class. See application.properties
 *
 * @author mckelvym
 * @since Nov 23, 2017
 */
@Data
@Getter
@ToString
@EnableConfigurationProperties
@ConfigurationProperties("tsp")
@Configuration
public class Properties
{
	/**
	 * The remote service URL
	 *
	 * @since Nov 23, 2017
	 */
	private String serviceUrl;
}
