package tsp.price.output;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Lists;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import tsp.price.data.Fund;
import tsp.price.data.Observation;

public class StdOutWriter implements ObservationWriter {

    private final List<Fund> funds;

    private PrintStream out;

    public StdOutWriter() {
        funds = new ArrayList<>(Fund.getOrdered());
    }

    @Override
    public void writeObservations(List<Observation> observations) {
        requireNonNull(observations);

        out = System.out;

        printHeader();
        observations.forEach(this::printObservation);
    }

    private void printHeader() {
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
        out.println(String.join(", ", elements));
    }
}
