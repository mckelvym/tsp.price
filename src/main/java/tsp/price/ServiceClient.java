package tsp.price;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Maps;

/**
 * Makes requests and parses them.
 *
 * @author mckelvym
 * @since Nov 23, 2017
 *
 */
@Service
public class ServiceClient
{
	/**
	 * Used to get the date for today
	 */
	private static final String	TODAY_MATCH_STRING	= "Daily as of";

	/**
	 * @since Mar 8, 2020
	 */
	private static final Logger	log					= LoggerFactory
			.getLogger(ServiceClient.class);

	/**
	 * Read the daily prices page and return a listing of entries.
	 *
	 * @param p_Request
	 *            the {@link Request}
	 * @return the listing of parsed entries.
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws ParseException
	 * @since Feb 16, 2016
	 */
	private static List<DataEntry> getDailyEntries(
			final Request p_Request)
			throws IOException, MalformedURLException, ParseException
	{
		final List<DataEntry> entries = new ArrayList<>();
		try (WebClient client = new WebClient())
		{
			log.info("Client create");
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);

			final URI uri = new DefaultUriBuilderFactory().expand(
					p_Request.toUriTemplate(), p_Request.toUriVariables());
			log.info("Connect to " + uri.toString());
			final HtmlPage page = client.getPage(uri.toString());
			page.cleanUp();

			String xPathExpr = "//table[@class='responsive-table']";
			log.info("xPath Expression: " + xPathExpr);
			final List<Object> matches = page.getByXPath(xPathExpr);
			log.info("Found " + matches.size() + " matches.");

			List<HtmlElement> tables = new ArrayList<>();

			for (Object match : matches)
			{
				if (match instanceof HtmlElement element)
				{
					tables.add(element);
				}
			}

			entries.addAll(processTables(tables));
		}
		return entries;
	}

	/**
	 * Process the HTML tables
	 * 
	 * @param tables
	 * @return {@link List} of {@link DataEntry}
	 */
	private static List<DataEntry> processTables(
			List<HtmlElement> tables)
	{
		List<DataEntry> entries = new ArrayList<>();
		AtomicReference<LocalDate> dateRef = new AtomicReference<>();
		for (final HtmlElement table : tables)
		{
			entries.addAll(processTable(table, dateRef));
		}
		return entries;
	}

	/**
	 * Process an HTML table
	 * 
	 * @param table
	 * @param dateRef
	 * @return {@link List} of {@link DataEntry}
	 */
	private static List<DataEntry> processTable(final HtmlElement table,
												AtomicReference<LocalDate> dateRef)
	{
		List<DataEntry> entries = new ArrayList<>();

		final CharMatcher numberMatcher = CharMatcher.digit()
				.or(CharMatcher.is('.'));

		if (dateRef.get() == null)
		{
			final List<HtmlElement> items = table.getElementsByTagName("th");
			log.info("Found " + items.size() + " headings.");
			for (HtmlElement item : items)
			{
				String textContent = item.getTextContent();
				if (textContent.contains(TODAY_MATCH_STRING))
				{
					String dateS = textContent.replace(TODAY_MATCH_STRING, "")
							.trim();
					log.info("Date: " + dateS);

					LocalDate date = LocalDate.parse(dateS,
							DateTimeFormatter.ofPattern("MMMM d, yyyy"));
					dateRef.set(date);
					log.info("Parsed date: " + date);
					break;
				}
			}
		}

		List<HtmlElement> rows = new ArrayList<>(table.getElementsByTagName("tr"));
		for (HtmlElement row : rows)
		{
			List<HtmlElement> headings = new ArrayList<>(row.getElementsByTagName("th"));
			List<HtmlElement> dataElements = new ArrayList<>(row.getElementsByTagName("td"));

			for (HtmlElement heading : headings)
			{
				List<HtmlElement> headingLinks = new ArrayList<>(heading.getElementsByTagName("a"));
				if (headingLinks.size() > 0)
				{
					for (HtmlElement headingLink : headingLinks)
					{
						String headingS = headingLink.getTextContent();

						Optional<Fund> tryValueOf = Fund.tryValueOf(headingS);
						if (tryValueOf.isPresent()
								&& tryValueOf.get() != Fund.NOOP)
						{
							final String nameS = headingS;
							final String valueS = dataElements.remove(0)
									.getTextContent().split("\\$")[1];
							final String changeS = dataElements.remove(0)
									.getTextContent();
							final String changePcS = dataElements.remove(0)
									.getTextContent();

							final String valueS_Cleaned = numberMatcher
									.retainFrom(valueS);
							final String changeS_Cleaned = numberMatcher
									.retainFrom(changeS);
							final String changePcS_Cleaned = numberMatcher
									.retainFrom(changePcS);

							final Number value = Double
									.parseDouble(valueS_Cleaned);
							final Number change = Double
									.parseDouble(changeS_Cleaned);
							final Number changePc = Double
									.parseDouble(changePcS_Cleaned);

							final DataEntry entry = DataEntry
									.builder().name(nameS).value(value)
									.change(change).changePercent(changePc)
									.date(dateRef.get()).fund(tryValueOf.get())
									.build();
							log.info(String.format(
									"Build entry from name=%s value=%s change=%s changePercent=%s date=%s; fund=%s",
									nameS, value, change, changePc,
									dateRef.get(), entry.getFund()));
							entries.add(entry);
							break;
						}
					}
				}
			}
		}

		return entries;
	}

	/**
	 * Query the server for {@link Response}
	 *
	 * @param p_Request
	 *            the {@link Request} request parameters
	 * @return the {@link Response} returned
	 * @throws RestClientException,
	 *             HttpClientErrorException
	 * @since Nov 23, 2017
	 */
	public Response get(final Request p_Request) throws RestClientException
	{
		try
		{
			final List<DataEntry> entries = getDailyEntries(p_Request);
			if (!entries.isEmpty())
			{
				final LocalDate date = entries.get(0).getDate();
				final Map<Fund, Number> values = Maps.newLinkedHashMap();

				for (DataEntry entry : entries)
				{
					Fund fund = entry.getFund();
					if (fund != Fund.NOOP)
					{
						Number value = entry.getValue();
						log.info(fund + ": " + value);
						values.put(fund, value);
					}
				}
				final Observation observation = Observation.builder().date(date)
						.value(values).build();
				return Response.builder()
						.observations(Arrays.asList(observation)).build();
			}
		}
		catch (final MalformedURLException e)
		{
			final String message = "Unable to get daily entries".formatted();
			throw new RestClientException(message, e);
		}
		catch (final IOException e)
		{
			final String message = "Unable to get daily entries".formatted();
			throw new RestClientException(message, e);
		}
		catch (final ParseException e)
		{
			final String message = "Unable to get daily entries".formatted();
			throw new RestClientException(message, e);
		}
		return Response.builder().build();
	}

}
