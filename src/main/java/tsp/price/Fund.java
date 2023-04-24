package tsp.price;

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
 * @since Nov 19, 2017
 *
 */
@SuppressWarnings("javadoc")
public enum Fund
{
	ACTIVE_CONSERVATIVE
	{
		@Override
		public String getDescription()
		{
			return "Active Conservative";
		}
	},
	ACTIVE_DIVERSIFIED_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Active Diversified Equity";
		}
	},
	ACTIVE_DIVERSIFIED_FIXED_INCOME
	{
		@Override
		public String getDescription()
		{
			return "Active Diversified Fixed Income";
		}
	},
	ACTIVE_GROWTH
	{
		@Override
		public String getDescription()
		{
			return "Active Growth";
		}
	},
	ACTIVE_INTERNATIONAL_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Active International Equity";
		}
	},
	ACTIVE_MODERATE_GROWTH
	{
		@Override
		public String getDescription()
		{
			return "Active Moderate Growth";
		}
	},
	INDEX_BOND
	{
		@Override
		public String getDescription()
		{
			return "Index Bond";
		}
	},
	INDEX_INTERNATIONAL_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Index International Equity";
		}
	},
	INDEX_US_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Index U.S. Equity";
		}
	},
	INDEX_US_LARGE_CAP_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Index U.S. Large Cap Equity";
		}
	},
	NOOP
	{
		@Override
		public String getDescription()
		{
			return "N/A";
		}
	},
	PASSIVE_CONSERVATIVE
	{
		@Override
		public String getDescription()
		{
			return "Passive Conservative";
		}
	},
	PASSIVE_DIVERSIFIED_EQUITY
	{
		@Override
		public String getDescription()
		{
			return "Passive Diversified Equity";
		}
	},
	PASSIVE_DIVERSIFIED_FIXED_INCOME
	{
		@Override
		public String getDescription()
		{
			return "Passive Diversified Fixed Income";
		}
	},
	PASSIVE_GROWTH
	{
		@Override
		public String getDescription()
		{
			return "Passive Growth";
		}
	},
	PASSIVE_MODERATE_GROWTH
	{
		@Override
		public String getDescription()
		{
			return "Passive Moderate Growth";
		}
	},
	PRINCIPAL_PLUS_INTEREST
	{
		@Override
		public String getDescription()
		{
			return "Principal Plus Interest";
		}
	},
	SOCIAL_CHOICE
	{
		@Override
		public String getDescription()
		{
			return "Social Choice";
		}
	},;

	private static Map<String, Fund> map = Maps.newLinkedHashMap();

	static
	{
		getOrdered().forEach(fund ->
		{
			map.put(fund.getDescription().toLowerCase(), fund);
		});
	}

	/**
	 * @return ordered List of Fund
	 * @since Nov 19, 2017
	 */
	public static List<Fund> getOrdered()
	{
		return Arrays.asList(PRINCIPAL_PLUS_INTEREST, ACTIVE_DIVERSIFIED_EQUITY,
				ACTIVE_GROWTH, ACTIVE_MODERATE_GROWTH, ACTIVE_CONSERVATIVE,
				ACTIVE_DIVERSIFIED_FIXED_INCOME, ACTIVE_INTERNATIONAL_EQUITY,
				PASSIVE_DIVERSIFIED_EQUITY, PASSIVE_GROWTH,
				PASSIVE_MODERATE_GROWTH, PASSIVE_CONSERVATIVE,
				PASSIVE_DIVERSIFIED_FIXED_INCOME, INDEX_INTERNATIONAL_EQUITY,
				SOCIAL_CHOICE, INDEX_BOND, INDEX_US_LARGE_CAP_EQUITY,
				INDEX_US_EQUITY);
	}

	/**
	 * Use partial match to find {@link Fund}
	 *
	 * @param p_String
	 *            the uppercase string
	 * @return the fund, if found.
	 * @since Mar 8, 2020
	 */
	public static Optional<Fund> tryValueOf(final String p_String)
	{
		String string = p_String.toLowerCase();
		final Fund fund = map.get(string);
		if (fund != null)
		{
			return Optional.of(fund);
		}

		for (final Entry<String, Fund> entry : map.entrySet())
		{
			String key = entry.getKey();
			Fund value = entry.getValue();
			if (string.contains(key))
			{
//				System.out.println(
//						value + ": " + string + " contains " + key + " (1)");
				return Optional.of(value);
			}
			if (key.contains(string))
			{
//				System.out.println(
//						value + ": " + key + " contains " + string + " (2)");
				return Optional.of(value);
			}
		}

		return Optional.empty();
	}

	/**
	 * @return description of this fund.
	 * @since Nov 24, 2017
	 */
	public abstract String getDescription();
}
