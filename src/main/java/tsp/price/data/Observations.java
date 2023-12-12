package tsp.price.data;

import java.util.Iterator;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Collection of {@link Observation}
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Data
@Builder
public class Observations implements Iterable<Observation> {
    /**
     * The collection of {@link Observation}
     *
     * @since Apr 24, 2023
     */
    private final List<Observation> observations;

    @Override
    public Iterator<Observation> iterator() {
        return getObservations().iterator();
    }
}
