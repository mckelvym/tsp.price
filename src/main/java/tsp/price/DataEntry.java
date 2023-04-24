package tsp.price;

import com.google.common.base.CharMatcher;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A single entry
 *
 * @author mckelvym
 * @since Nov 24, 2017
 *
 */
@Data
@RequiredArgsConstructor
@Builder
public class DataEntry
{
	/**
	 * @since Nov 24, 2017
	 */
	@NonNull
	private Number		change;

	/**
	 * @since Nov 24, 2017
	 */
	@NonNull
	private Number		changePercent;

	/**
	 * @since Nov 24, 2017
	 */
	@NonNull
	private LocalDate	date;

	/**
	 * @since Dec 24, 2022
	 */
	@NonNull
	private Fund		fund;

	/**
	 * @since Nov 24, 2017
	 */
	@NonNull
	private String		name;

	/**
	 * @since Nov 24, 2017
	 */
	@NonNull
	private Number		value;

	/**
	 * @return the {@link Fund} if it could be matched, otherwise
	 *         {@link Fund#NOOP}
	 * @since Nov 24, 2017
	 */
	public Fund getFundFromName()
	{
		String entryName = CharMatcher.whitespace()
				.replaceFrom(String.format("%s", getName()), ' ');

		entryName = CharMatcher.javaLetterOrDigit().or(CharMatcher.is('_'))
				.retainFrom(CharMatcher.whitespace().or(CharMatcher.is('-'))
						.replaceFrom(entryName, "_"))
				.toUpperCase();

		final Optional<Fund> tryValueOf = Fund.tryValueOf(entryName);
		return tryValueOf.orElse(Fund.NOOP);
	}
}
