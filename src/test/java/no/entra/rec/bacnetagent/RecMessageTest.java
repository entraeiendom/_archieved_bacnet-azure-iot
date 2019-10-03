package no.entra.rec.bacnetagent;

import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;

import static no.entra.rec.bacnetagent.ObservationTypes.TEMPERATURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class RecMessageTest {
    private static final Logger log = getLogger(RecMessageTest.class);

    private final String expectedJson = "{\n" +
            "  \"format\": \"rec3.1\",\n" +
            "  \"deviceId\": \"https://recref.com/device/sensorGateway1\",\n" +
            "  \"observations\": [\n" +
            "    {\n" +
            "      \"observationTime\": \"2019-05-27T20:07:44Z\",\n" +
            "      \"value\": 16.3,\n" +
            "      \"quantityKind\": \"https://w3id.org/rec/core/Temperature\",\n" +
            "      \"sensorId\" : \"https://recref.com/sensor/tempSensor1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void getDeviceId() {
        String deviceId = "";
        RecMessage message = new RecMessage(deviceId);
        assertEquals("https://recref.com/device/" + deviceId, message.getDeviceId());
    }

    @Test
    public void getTemperatureObservation() {
        String deviceId = "sensorGateway1";
        RecMessage message = new RecMessage(deviceId);
        String sensorId = "tempSensor1";
        String quantityKind = TEMPERATURE;
        TemperatureObservation observation = new TemperatureObservation(sensorId, 16.3);
        message.addObservation(observation);
        assertEquals("https://recref.com/device/" + deviceId, message.getDeviceId());
        assertEquals(16.3, observation.getValue());
        assertNotNull(message.getObservations());
        assertEquals(message.getObservations()[0].getValue(), 16.3);
    }

    @Test
    public void getTemperatureObservationAsJson() throws JSONException {
        Gson gson = new Gson();
        String deviceId = "sensorGateway1";
        RecMessage message = new RecMessage(deviceId);
        String sensorId = "tempSensor1";
        TemperatureObservation observation = new TemperatureObservation(sensorId, 16.3);
        message.addObservation(observation);

        String messageJson= gson.toJson(message);
        log.debug("actual json: \n {}", messageJson);
        JSONAssert.assertEquals(expectedJson, messageJson,false);
    }
}