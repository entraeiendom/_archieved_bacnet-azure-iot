package no.entra.rec.bacnetagent;

import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;
import no.entra.rec.bacnetagent.azureiot.SendReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;

import static no.entra.rec.bacnetagent.azureiot.SendReceive.D2C_MESSAGE_TIMEOUT;
import static no.entra.rec.bacnetagent.azureiot.SendReceive.failedMessageListOnClose;
import static no.entra.rec.bacnetagent.utils.PropertyReader.findProperty;


public class SendDeviceToCloudMessage {
    private static final Logger log = LoggerFactory.getLogger(SendDeviceToCloudMessage.class);
    private static final String DEVICE_CONNECTION_STRING = "DEVICE_CONNECTION_STRING";

    /**
     * Send an Event Message to Azure IoT Hub
     *
     * @param args args[0] = IoT Hub connection string
     */
    public static void main(String[] args)
            throws IOException, URISyntaxException {

        log.debug("Starting...");
        log.debug("Beginning setup.");

        Gson gson = new Gson();

        String deviceConnectionString = findProperty(DEVICE_CONNECTION_STRING);
        if (deviceConnectionString == null) {
            if (args.length > 0) {
                deviceConnectionString = args[0];
            }
        }

        if (isEmpty(deviceConnectionString)) {
            log.error("Missing required property: DEVICE_CONNECTION_STRING, exiting ");
            System.exit(1);
        }

        log.debug("Successfully read input parameters.");
        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
        log.debug("Using communication protocol %s.\n", protocol.name());

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

        log.debug("Opened connection to IoT Hub.");
        log.debug("Beginning to receive messages...");
        log.debug("Sending the following event messages: ");
        log.debug("Updated token expiry time to " + time);


        String deviceId = client.getConfig().getDeviceId();

        double temperature = 20 + Math.random() * 10;
        double humidity = 30 + Math.random() * 20;
        RecMessage recMessage = new RecMessage(deviceId);
        Observation temperatureObservation = new TemperatureObservation("temperatureSensor1", temperature);
        recMessage.addObservation(temperatureObservation);
        String messageId = UUID.randomUUID().toString();
        IoTEdgeMessage ioTEdgeMessage = new IoTEdgeMessage(deviceId, messageId, recMessage);

        String msgStr = gson.toJson(ioTEdgeMessage); //"{\"deviceId\":\"" + deviceId + "\",\"messageId\":" + messageId + ",\"temperature\":" + temperature + ",\"humidity\":" + humidity + "}";

        try {
            Message msg = new Message(msgStr);
            msg.setContentTypeFinal("application/json");
            msg.setProperty("temperatureAlert", temperature > 28 ? "true" : "false");
            msg.setMessageId(java.util.UUID.randomUUID().toString());
            msg.setExpiryTime(D2C_MESSAGE_TIMEOUT);
            log.debug(msgStr);
            SendReceive.EventCallback eventCallback = new SendReceive.EventCallback();
            client.sendEventAsync(msg, eventCallback, msg);
        } catch (Exception e) {
            e.printStackTrace(); // Trace the exception
        }


        log.debug("Wait for " + D2C_MESSAGE_TIMEOUT / 1000 + " second(s) for response from the IoT Hub...");

        // Wait for IoT Hub to respond.
        try {
            Thread.sleep(D2C_MESSAGE_TIMEOUT);
        } catch (InterruptedException e) {
            log.debug("timeout interupted {}", e.getMessage());
        }

        log.info("In receive mode. Waiting for receiving C2D messages. Press ENTER to close");
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
        return value == null || value.isEmpty();
    }
}
