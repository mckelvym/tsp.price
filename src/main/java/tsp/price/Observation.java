package tsp.price;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ComparisonChain;

import java.time.LocalDate;
import java.util.Map;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A single observation entry.
 *
 * @author mckelvym
 * @since Nov 23, 2017
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
public class Observation implements Comparable<Observation>
{
	/**
	 * The observation timestamp.
	 *
	 * @author mckelvym
	 * @since Nov 23, 2017
	 */
	private final LocalDate			date;

	/**
	 * @since Nov 23, 2017
	 */
	private final Map<Fund, Number>	value;

	@Override
	public int compareTo(final Observation p_Other)
	{
		return ComparisonChain.start().compare(getDate(), p_Other.getDate())
				.result();
	}

}
