package tsp.price.data;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * The fund types.
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
public enum Fund {
    C_FUND {
        @Override
        public String getDescription() {
            return "C Fund";
        }
    },
    F_FUND {
        @Override
        public String getDescription() {
            return "F Fund";
        }
    },
    G_FUND {
        @Override
        public String getDescription() {
            return "G Fund";
        }
    },
    I_FUND {
        @Override
        public String getDescription() {
            return "I Fund";
        }
    },
    S_FUND {
        @Override
        public String getDescription() {
            return "S Fund";
        }
    },
    NOOP {
        @Override
        public String getDescription() {
            return "N/A";
        }
    },
    ;

    private static final Map<String, Fund> map = Maps.newLinkedHashMap();

    static {
        getOrdered().forEach(fund ->
                map.put(fund.getDescription().toLowerCase(), fund));
    }

    /**
     * @return ordered List of Fund
     * @since Apr 24, 2023
     */
    public static List<Fund> getOrdered() {
        return Arrays.asList(G_FUND, F_FUND, C_FUND, S_FUND, I_FUND);
    }

    /**
     * Use partial match to find {@link Fund}
     *
     * @param p_String the uppercase string
     * @return the fund, if found.
     * @since Apr 24, 2023
     */
    public static Optional<Fund> tryValueOf(final String p_String) {
        String string = p_String.toLowerCase();
        final Fund fund = map.get(string);
        if (fund != null) {
            return Optional.of(fund);
        }

        for (final Entry<String, Fund> entry : map.entrySet()) {
            String key = entry.getKey();
            Fund value = entry.getValue();
            if (string.contains(key)) {
                return Optional.of(value);
            }
            if (key.contains(string)) {
                return Optional.of(value);
            }
        }

        if (p_String.startsWith("L ")) {
            return Optional.of(NOOP);
        }

        return Optional.empty();
    }

    /**
     * @return description of this fund.
     * @since Nov 24, 2017
     */
    public abstract String getDescription();
}
