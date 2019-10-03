package no.entra.rec.bacnetagent;

import static no.entra.rec.bacnetagent.ObservationTypes.TEMPERATURE;

public class TemperatureObservation extends Observation {

    private final double value;

    public TemperatureObservation(String sensorId,  double value) {
        super( sensorId, TEMPERATURE);
        this.value = value;
    }

    @Override
    Object getValue() {
        return value;
    }
}
