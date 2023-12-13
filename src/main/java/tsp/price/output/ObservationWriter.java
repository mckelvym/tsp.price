package tsp.price.output;

import java.util.List;
import tsp.price.data.Observation;

public interface ObservationWriter {
    void writeObservations(List<Observation> observations);
}
