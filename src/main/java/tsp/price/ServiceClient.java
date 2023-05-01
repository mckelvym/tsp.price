package tsp.price;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Makes requests and parses them.
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Service
public class ServiceClient {
    /**
     * Used to get the date for today
     */
    private static final String TODAY_MATCH_STRING = "Daily as of";

    /**
     * @since Apr 24, 2023
     */
    private static final Logger log = LoggerFactory
            .getLogger(ServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Read the daily prices page and return a listing of entries.
     *
     * @param p_Request the {@link Request}
     * @return the listing of parsed entries.
     * @throws IOException
     * @throws MalformedURLException
     * @throws ParseException
     * @since Apr 24, 2023
     */
    private List<DataEntry> getDailyEntries(
            final Request p_Request)
            throws IOException, MalformedURLException, ParseException {
        final List<DataEntry> entries = new ArrayList<>();

        final URI uri = new DefaultUriBuilderFactory().expand(
                p_Request.toUriTemplate(), p_Request.toUriVariables());
        log.info("Connect to " + uri.toString());

        ResponseEntity<Response> response = restTemplate.getForEntity(uri, Response.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return entries;
        }

        Response body = response.getBody();
        String key = body.keySet().iterator().next();
        LocalDate date = LocalDate.parse(key,
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Map<String, Double> fundValues = body.get(key);

        for (Map.Entry<String, Double> entry : fundValues.entrySet()) {
            String fundName = entry.getKey();
            Optional<Fund> fundOpt = Fund.tryValueOf(fundName);
            if (fundOpt.isEmpty()) {
                log.error("Unable to parse '%s'".formatted(fundName));
                continue;
            }
            DataEntry dataEntry = DataEntry.builder().date(date).fund(fundOpt.get()).value(entry.getValue()).name(fundName).build();
            log.info(String.valueOf(dataEntry));
            entries.add(dataEntry);
        }

        return entries;
    }

    /**
     * Query the server for {@link Observations}
     *
     * @param p_Request the {@link Request} request parameters
     * @return the {@link Observations} returned
     * @throws RestClientException, HttpClientErrorException
     * @since Apr 24, 2023
     */
    public Observations get(final Request p_Request) throws RestClientException {
        try {
            final List<DataEntry> entries = getDailyEntries(p_Request);
            if (!entries.isEmpty()) {
                final LocalDate date = entries.get(0).getDate();
                final Map<Fund, Number> values = Maps.newLinkedHashMap();

                for (DataEntry entry : entries) {
                    Fund fund = entry.getFund();
                    if (fund != Fund.NOOP) {
                        Number value = entry.getValue();
                        log.info(fund + ": " + value);
                        values.put(fund, value);
                    }
                }
                final Observation observation = Observation.builder().date(date)
                        .value(values).build();
                return Observations.builder()
                        .observations(Arrays.asList(observation)).build();
            }
        } catch (final MalformedURLException e) {
            final String message = "Unable to get daily entries".formatted();
            throw new RestClientException(message, e);
        } catch (final IOException e) {
            final String message = "Unable to get daily entries".formatted();
            throw new RestClientException(message, e);
        } catch (final ParseException e) {
            final String message = "Unable to get daily entries".formatted();
            throw new RestClientException(message, e);
        }
        return Observations.builder().build();
    }
}
