package tsp.price.output;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.price.config.OutputConfig;
import tsp.price.data.Fund;
import tsp.price.data.Observation;

public class CsvWriter implements ObservationWriter {

    private static final Logger log = LoggerFactory
            .getLogger(CsvWriter.class);

    private final OutputConfig outputConfig;

    private final List<Fund> funds;

    private BufferedWriter writer;

    public CsvWriter(OutputConfig outputConfig) {
        this.outputConfig = requireNonNull(outputConfig);
        funds = new ArrayList<>(Fund.getOrdered());
    }

    @Override
    public void writeObservations(List<Observation> observations) {
        requireNonNull(observations);

        final Optional<File> outFileOpt = outputConfig.outFile();
        if (outFileOpt.isEmpty()) {
            return;
        }
        log.info("Write to " + outFileOpt.get());
        try (BufferedWriter writer = Files.newWriter(outFileOpt.get(),
                Charset.defaultCharset())) {
            this.writer = writer;
            printHeader();
            observations.forEach(this::printObservation);

            processMergeObservations(observations, outputConfig);

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

    private void processMergeObservations(List<Observation> observations, OutputConfig outputConfig) throws IOException {
        Set<LocalDate> dates = observations.stream()
                .map(Observation::getDate)
                .collect(Collectors.toSet());
        outputConfig.mergeObservations().stream()
                .filter(o -> !dates.contains(o.getDate()))
                .forEach(this::printObservation);
    }

    private void printHeader() throws IOException {
        List<String> elements = Lists.newArrayList("Date");
        funds.stream().map(Fund::getDescription).forEach(elements::add);
        printRow(elements);
    }

    private void printObservation(Observation o) {
        final List<String> elements = Lists.newArrayList(String.valueOf(o.getDate()));
        funds.stream()
                .map(o::getFormattedValue)
                .forEach(elements::add);
        printRow(elements);
    }

    private void printRow(List<String> elements) {
        try {
            writer.write(String.join(", ", elements));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
