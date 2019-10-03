package no.entra.rec.bacnetagent;

import java.util.ArrayList;
import java.util.Optional;

public class RecMessage {
    private final String format = "rec3.1";
    private final String deviceId;
    private Optional<ArrayList<Observation>> observations;

    public RecMessage(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getDeviceId() {
        return "https://recref.com/device/" + deviceId;
    }

    public void addObservation(Observation observation) {
        if (observations == null) {
            observations = Optional.of(new ArrayList<>());
        }
        observations.get().add(observation);
    }

    public Optional<ArrayList<Observation>> getObservations() {
        return observations;
    }
}
