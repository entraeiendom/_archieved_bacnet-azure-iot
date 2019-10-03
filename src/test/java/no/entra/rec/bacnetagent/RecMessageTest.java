package no.entra.rec.bacnetagent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecMessageTest {

    @Test
    public void getDeviceId() {
        String deviceId = "sensorGateway1";
        RecMessage message = new RecMessage(deviceId);
        assertEquals("https://recref.com/device/" + deviceId, message.getDeviceId());
    }
}