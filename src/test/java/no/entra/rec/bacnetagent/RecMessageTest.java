package no.entra.rec.bacnetagent;

import org.junit.Test;

import static no.entra.rec.bacnetagent.ObservationTypes.TEMPERATURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecMessageTest {

    @Test
    public void getDeviceId() {
        String deviceId = "sensorGateway1";
        RecMessage message = new RecMessage(deviceId);
        assertEquals("https://recref.com/device/" + deviceId, message.getDeviceId());
    }

    @Test
    public void getTemperatureObservation() {
        String deviceId = "sensorGateway1";
        RecMessage message = new RecMessage(deviceId);
        String sensorId = "tempSensor1";
        String quantityKind = TEMPERATURE;
        TemperatureObservation observation = new TemperatureObservation(sensorId, quantityKind, 16.3);
        message.addObservation(observation);
        assertEquals("https://recref.com/device/" + deviceId, message.getDeviceId());
        assertEquals(16.3, observation.getValue());
        assertTrue(message.getObservations().isPresent());
        assertEquals(message.getObservations().get().get(0).getValue(), 16.3);
    }
}