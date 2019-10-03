package no.entra.rec.bacnetagent;

public class TemperatureObservation extends Observation {

    private final double value;

    public TemperatureObservation(String sensorId, String quantityKind, double value) {
        super(sensorId, quantityKind);
        this.value = value;
    }

    @Override
    Object getValue() {
        return value;
    }
}
