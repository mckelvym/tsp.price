package tsp.price;

import com.google.common.base.CharMatcher;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A single entry
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Data
@RequiredArgsConstructor
@Builder
@ToString
public class DataEntry {
    /**
     * @since Apr 24, 2023
     */
    @NonNull
    private LocalDate date;

    /**
     * @since Apr 24, 2023
     */
    @NonNull
    private Fund fund;

    /**
     * @since Apr 24, 2023
     */
    @NonNull
    private String name;

    /**
     * @since Apr 24, 2023
     */
    @NonNull
    private Number value;

    /**
     * @return the {@link Fund} if it could be matched, otherwise
     * {@link Fund#NOOP}
     * @since Apr 24, 2023
     */
    public Fund getFundFromName() {
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
