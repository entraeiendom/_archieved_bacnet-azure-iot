package no.entra.rec.bacnetagent;

public abstract class Observation {
    private final String sensorId;
    private final String quantityKind;

    public Observation(String sensorId, String quantityKind) {
        this.sensorId = sensorId;
        this.quantityKind = quantityKind;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getQuantityKind() {
        return quantityKind;
    }
    abstract Object getValue();
}
