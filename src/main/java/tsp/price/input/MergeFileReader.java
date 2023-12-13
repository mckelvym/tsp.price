package tsp.price.input;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.price.data.Fund;
import tsp.price.data.Observation;

public class MergeFileReader {

    private static final Logger log = LoggerFactory
            .getLogger(MergeFileReader.class);

    private final List<Fund> funds;
    private final Splitter splitter;

    public MergeFileReader() {
        funds = new ArrayList<>(Fund.getOrdered());
        splitter = Splitter.on(",");
    }

    public List<Observation> parse(String mergeFilePath) {
        return parse(new File(mergeFilePath));
    }

    public List<Observation> parse(File mergeFile) {
        requireNonNull(mergeFile);
        checkState(mergeFile.exists(), "Merge file does not exist.");

        log.info("Read from: " + mergeFile);
        try {
            final List<String> lines = Files.readLines(mergeFile, Charset.defaultCharset());
            List<Observation> observations = new ArrayList<>(lines.size() - 1);
            boolean headerSkipped = false;
            for (String line : lines) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                final List<String> elements = splitter.splitToList(line);
                final Observation observation = toObservation(elements);
                observations.add(observation);
            }
            return observations;
        } catch (IOException e) {
            log.error("Unable to process merge file.", e);
        }
        return List.of();
    }

    private Observation toObservation(List<String> elements) {
        final Observation.ObservationBuilder builder = Observation.builder();
        builder.date(LocalDate.parse(elements.get(0),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Map<Fund, Number> values = Maps.newLinkedHashMap();
        int index = 1;
        for (Fund fund : funds) {
            try {
                double value = Double.parseDouble(elements.get(index++));
                values.put(fund, value);
            } catch (NumberFormatException e) {
                values.put(fund, null);
            }
        }
        builder.value(values);

        return builder.build();
    }
}
