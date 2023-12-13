package tsp.price.data;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
}
