package tsp.price.config;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import tsp.price.data.Observation;

public record OutputConfig(List<Observation> mergeObservations, Optional<String> outFilePath) {
    public OutputConfig(List<Observation> mergeObservations, Optional<String> outFilePath) {
        this.mergeObservations = firstNonNull(mergeObservations, List.of());
        this.outFilePath = firstNonNull(outFilePath, empty());
    }

    public OutputConfig(List<Observation> mergeObservations, String outFilePath) {
        this(mergeObservations, ofNullable(outFilePath));
    }

    public Optional<File> outFile() {
        return outFilePath().map(File::new);
    }
}
