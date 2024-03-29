package tsp.price.xfer;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tsp.price.data.Observation;

/**
 * Collection of {@link Observation}
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class Response extends LinkedHashMap<String, Map<String, Double>> implements Map<String, Map<String, Double>> {
}
