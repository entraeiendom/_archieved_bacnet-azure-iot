package no.entra.rec.bacnetagent;

import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;
import no.entra.rec.bacnetagent.azureiot.SendReceive;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;

import static no.entra.rec.bacnetagent.azureiot.SendReceive.D2C_MESSAGE_TIMEOUT;
import static no.entra.rec.bacnetagent.azureiot.SendReceive.failedMessageListOnClose;
import static no.entra.rec.bacnetagent.utils.PropertyReader.findProperty;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Agent observing Bacnet objects sent on UDP.
 * These objects are then forwarded to the IoT Hub using RealEstateCore protocol.
 */
public class BacnetAgentDeamon {
    private static final Logger log = getLogger(BacnetAgentDeamon.class);
    public static final String DEVICE_CONNECTION_STRING = "DEVICE_CONNECTION_STRING";

    /**
     * Send observations to an IoT Hub using MQTT.
     * Receive Activation commands using: TODO
     *
     */
    public static void main(String[] args)
            throws IOException, URISyntaxException {
        //Enable INFO level logging, including timestamps. Uncomment the other log levels to get debug or trace level logs as well
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        //System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        //System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS");

        log.debug("Starting...");
        log.debug("Beginning setup.");

        Gson gson = new Gson();
        String deviceConnectionString = findProperty(DEVICE_CONNECTION_STRING);
        if (isEmpty(deviceConnectionString)) {
            if (args.length > 0) {
                deviceConnectionString = args[0];
            }
            if (isEmpty(deviceConnectionString)) {
                throw new IllegalArgumentException("Missing required environment variable: " + DEVICE_CONNECTION_STRING);
            }
        }


        int numberOfTestObservations = 5;

        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

        log.info("Successfully read input parameters.");
        log.info("Using communication protocol {}.\n", protocol.name());

        DeviceClient client = new DeviceClient(deviceConnectionString, protocol);

        log.debug("Successfully created an IoT Hub client.");

        SendReceive.MessageCallbackMqtt callback = new SendReceive.MessageCallbackMqtt();
        SendReceive.Counter counter = new SendReceive.Counter(0);
        client.setMessageCallback(callback, counter);


        log.debug("Successfully set message callback.");

        // Set your token expiry time limit here
        long time = 2400;
        client.setOption("SetSASTokenExpiryTime", time);
        client.registerConnectionStatusChangeCallback(new SendReceive.IotHubConnectionStatusChangeCallbackLogger(), new Object());
        client.open();

        log.info("Opened connection to IoT Hub.");
        log.debug("Beginning to receive messages...");
        log.debug("Sending the following event messages: ");
        log.debug("Updated token expiry time to " + time);

        String deviceId = client.getConfig().getDeviceId();

        for (int i = 0; i < numberOfTestObservations; ++i) {
            double temperature = 20 + Math.random() * 10;
            double humidity = 30 + Math.random() * 20;
            RecMessage recMessage = new RecMessage(deviceId);
            Observation temperatureObservation = new TemperatureObservation("temperatureSensor1", temperature);
            recMessage.addObservation(temperatureObservation);
            String messageId = UUID.randomUUID().toString();
            IoTEdgeMessage ioTEdgeMessage = new IoTEdgeMessage(deviceId, messageId, recMessage);

            String msgStr = gson.toJson(ioTEdgeMessage);
            try {
                Message msg = new Message(msgStr);
                msg.setContentTypeFinal("application/json");
                msg.setProperty("temperatureAlert", temperature > 28 ? "true" : "false");
                msg.setMessageId(messageId);
                msg.setExpiryTime(D2C_MESSAGE_TIMEOUT);
                log.debug(msgStr);
                SendReceive.EventCallback eventCallback = new SendReceive.EventCallback();
                client.sendEventAsync(msg, eventCallback, msg);
            } catch (Exception e) {
                e.printStackTrace(); // Trace the exception
            }

        }

        log.debug("Wait for " + D2C_MESSAGE_TIMEOUT / 1000 + " second(s) for response from the IoT Hub...");

        // Wait for IoT Hub to respond.
        try {
            Thread.sleep(D2C_MESSAGE_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("In receive mode. Waiting for receiving C2D messages. Press ENTER to close");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // close the connection
        log.debug("Closing");
        client.closeNow();

        if (!failedMessageListOnClose.isEmpty()) {
            log.debug("List of messages that were cancelled on close:" + failedMessageListOnClose.toString());
        }

        log.debug("Shutting down...");
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty() ;
    }
}
