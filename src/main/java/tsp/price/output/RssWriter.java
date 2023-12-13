package tsp.price.output;

import static java.util.Objects.requireNonNull;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.price.config.OutputConfig;
import tsp.price.config.Properties;
import tsp.price.data.Fund;
import tsp.price.data.Observation;

public class RssWriter implements ObservationWriter {

    private static final Logger log = LoggerFactory
            .getLogger(RssWriter.class);

    private final OutputConfig outputConfig;
    private final Properties properties;
    private final List<Fund> funds;
    private final List<SyndCategory> synedCatList;
    private final SyndFeed feed;

    public RssWriter(OutputConfig outputConfig, Properties properties) {
        this.outputConfig = requireNonNull(outputConfig);
        this.properties = requireNonNull(properties);
        funds = new ArrayList<>(Fund.getOrdered());

        feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("TSP Prices");
        feed.setLink(properties.getRepoUrl());
        feed.setDescription("Daily TSP Price Data");

        synedCatList = new ArrayList<>();
        SyndCategory syndCat = new SyndCategoryImpl();
        syndCat.setName("daily-prices");
        synedCatList.add(syndCat);
    }

    @Override
    public void writeObservations(List<Observation> observations) {
        requireNonNull(observations);

        final Optional<File> outFileOpt = outputConfig.outFile();
        if (outFileOpt.isEmpty()) {
            return;
        }
        log.info("Write to " + outFileOpt.get());

        List<SyndEntry> rssEntries = new ArrayList<>();
        observations.stream().map(this::toSyndEntry).forEach(rssEntries::add);
        rssEntries.addAll(processMergeFile(observations, outputConfig));
        feed.setEntries(rssEntries);

        SyndFeedOutput output = new SyndFeedOutput();
        try (Writer writer = new FileWriter(outFileOpt.get())) {
            output.output(feed, writer);
        } catch (FeedException e) {
            final String message = "Unable to create feed";
            log.error(message, e);
        } catch (final FileNotFoundException e) {
            final String message = "Unable to locate file: %s"
                    .formatted(outFileOpt.get());
            log.error(message, e);
        } catch (final IOException e) {
            final String message = "Unable to write to: %s"
                    .formatted(outFileOpt.get());
            log.error(message, e);
        }
    }

    private List<SyndEntry> processMergeFile(List<Observation> observations, OutputConfig outputConfig) {
        Set<LocalDate> dates = observations.stream()
                .map(Observation::getDate)
                .collect(Collectors.toSet());
        return outputConfig.mergeObservations().stream()
                .filter(o -> !dates.contains(o.getDate()))
                .map(this::toSyndEntry)
                .collect(Collectors.toList());
    }

    private SyndEntry toSyndEntry(Observation o) {
        SyndContent syndContent = new SyndContentImpl();
        syndContent.setType("text/html");

        StringBuilder table = new StringBuilder("<table><tbody>");
        final Map<Fund, Number> valueMap = o.getValue();
        for (Fund fund : funds) {
            final String description = fund.getDescription();
            final String valueS = o.getFormattedValue(fund);
            table.append("<tr><td>").append(description).append("</td><td>").append(valueS).append("</td></tr>");
        }
        table.append("</tbody></table>");

        syndContent.setValue(table.toString());
        final Date publishedDate = Date.from(o.getDate().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC));

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("Price Summary for " + o.getDate());
        entry.setLink(properties.getCsvUrl());
        entry.setPublishedDate(publishedDate);
        entry.setDescription(syndContent);
        entry.setCategories(synedCatList);
        return entry;
    }
}
