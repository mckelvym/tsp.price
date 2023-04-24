package tsp.price;

import java.util.Iterator;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Collection of {@link Observation}
 *
 * @author mckelvym
 * @since Nov 23, 2017
 *
 */
@Data
@Builder
public class Response implements Iterable<Observation>
{
	/**
	 * The collection of {@link Observation}
	 *
	 * @author mckelvym
	 * @since Nov 23, 2017
	 */
	private final List<Observation> observations;

	@Override
	public Iterator<Observation> iterator()
	{
		return getObservations().iterator();
	}
}
