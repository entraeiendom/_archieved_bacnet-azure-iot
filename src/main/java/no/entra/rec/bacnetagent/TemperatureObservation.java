package no.entra.rec.bacnetagent;

import java.time.Instant;

import static no.entra.rec.bacnetagent.ObservationTypes.TEMPERATURE;

public class TemperatureObservation extends Observation {

    private final double value;

    public TemperatureObservation(String sensorId,  double value) {
        super( REC_NS_SENSOR + sensorId, TEMPERATURE);
        this.value = value;
    }

    protected TemperatureObservation(String sensorId, double value, Instant observationTime) {
        super( REC_NS_SENSOR + sensorId, TEMPERATURE, observationTime);
        this.value = value;
    }

    @Override
    Object getValue() {
        return value;
    }
}
