package no.entra.rec.bacnetagent;

public abstract class Observation {
    private final String sensorId;
    private final String quantityKind;
    private final String observationTime = "2019-05-27T20:07:44Z";

    public Observation(String sensorId, String quantityKind) {
        this.sensorId = "https://recref.com/sensor/" + sensorId;
        if (quantityKind != null && quantityKind.startsWith("https://w3id.org/rec/core")) {
            this.quantityKind = quantityKind;
        } else {
            this.quantityKind = "https://recref.com/rec/core/" + quantityKind;
        }
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getQuantityKind() {
        return quantityKind;
    }
    abstract Object getValue();
}
