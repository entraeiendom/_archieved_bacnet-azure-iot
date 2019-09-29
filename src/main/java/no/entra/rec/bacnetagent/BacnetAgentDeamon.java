package no.entra.rec.bacnetagent;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;
import no.entra.rec.bacnetagent.azureiot.SendReceive;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static no.entra.rec.bacnetagent.azureiot.SendReceive.D2C_MESSAGE_TIMEOUT;
import static no.entra.rec.bacnetagent.azureiot.SendReceive.failedMessageListOnClose;

/**
 * Hello world!
 */
public class BacnetAgentDeamon {
    /**
     * Receives requests from an IoT Hub. Default protocol is to use
     * use MQTT transport.
     *
     * @param args args[0] = IoT Hub connection string
     *             args[1] = number of requests to send
     *             args[2] = protocol (optional, one of 'mqtt' or 'amqps' or 'https' or 'amqps_ws')
     *             args[3] = path to certificate to enable one-way authentication over ssl for amqps (optional, default shall be used if unspecified).
     */

    public static void main(String[] args)
            throws IOException, URISyntaxException {
        //TODO accept docker environment variables
        //Enable INFO level logging, including timestamps. Uncomment the other log levels to get debug or trace level logs as well
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        //System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        //System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS");

        System.out.println("Starting...");
        System.out.println("Beginning setup.");

        String deviceConnectionString = System.getenv("DEVICE_CONNECTION_STRING");
        if (deviceConnectionString == null) {
            deviceConnectionString = args[0];
        }


//        String connString = args[0];
        int numRequests;
        try {
            String numRequestsString = System.getenv("NUMBER_REQUESTS");
            if (numRequestsString != null) {
                numRequests = Integer.parseInt(numRequestsString);
            } else {
                numRequests = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            System.out.format(
                    "Could not parse the number of requests to send. "
                            + "Expected an int but received:\n%s.\n", args[1]);
            return;
        }
        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

        String pathToCertificate = null;
        if (isEmpty(deviceConnectionString) || numRequests < 0) {
            System.out.format(
                    "Expected 2 or 3 arguments but received: %d.\n"
                            + "The program should be called with the following args: \n"
                            + "1. [Device connection string] - String containing Hostname, Device Id & Device Key in one of the following formats: HostName=<iothub_host_name>;DeviceId=<device_id>;SharedAccessKey=<device_key>\n"
                            + "2. [number of requests to send]\n"
                            + "3. (mqtt | https | amqps | amqps_ws | mqtt_ws)\n"
                            + "4. (optional) path to certificate to enable one-way authentication over ssl for amqps \n",
                    args.length);
            return;
        }
        /*
        if (args.length == 2) {
            protocol = IotHubClientProtocol.MQTT;
        } else {
            String protocolStr = args[2];
            if (protocolStr.equals("https")) {
                protocol = IotHubClientProtocol.HTTPS;
            } else if (protocolStr.equals("amqps")) {
                protocol = IotHubClientProtocol.AMQPS;
            } else if (protocolStr.equals("mqtt")) {
                protocol = IotHubClientProtocol.MQTT;
            } else if (protocolStr.equals("amqps_ws")) {
                protocol = IotHubClientProtocol.AMQPS_WS;
            } else if (protocolStr.equals("mqtt_ws")) {
                protocol = IotHubClientProtocol.MQTT_WS;
            } else {
                System.out.format(
                        "Expected argument 2 to be one of 'mqtt', 'https', 'amqps' or 'amqps_ws' but received %s\n"
                                + "The program should be called with the following args: \n"
                                + "1. [Device connection string] - String containing Hostname, Device Id & Device Key in one of the following formats: HostName=<iothub_host_name>;DeviceId=<device_id>;SharedAccessKey=<device_key>\n"
                                + "2. [number of requests to send]\n"
                                + "3. (mqtt | https | amqps | amqps_ws | mqtt_ws)\n"
                                + "4. (optional) path to certificate to enable one-way authentication over ssl for amqps \n",
                        protocolStr);
                return;
            }
        }
        */

        System.out.println("Successfully read input parameters.");
        System.out.format("Using communication protocol %s.\n", protocol.name());

        DeviceClient client = new DeviceClient(deviceConnectionString, protocol);
//        if (pathToCertificate != null) {
//            client.setOption("SetCertificatePath", pathToCertificate);
//        }

        System.out.println("Successfully created an IoT Hub client.");

        if (protocol == IotHubClientProtocol.MQTT) {
            SendReceive.MessageCallbackMqtt callback = new SendReceive.MessageCallbackMqtt();
            SendReceive.Counter counter = new SendReceive.Counter(0);
            client.setMessageCallback(callback, counter);
        } else {
            SendReceive.MessageCallback callback = new SendReceive.MessageCallback();
            SendReceive.Counter counter = new SendReceive.Counter(0);
            client.setMessageCallback(callback, counter);
        }

        System.out.println("Successfully set message callback.");

        // Set your token expiry time limit here
        long time = 2400;
        client.setOption("SetSASTokenExpiryTime", time);

        client.registerConnectionStatusChangeCallback(new SendReceive.IotHubConnectionStatusChangeCallbackLogger(), new Object());

        client.open();

        System.out.println("Opened connection to IoT Hub.");

        System.out.println("Beginning to receive messages...");

        System.out.println("Sending the following event messages: ");

        System.out.println("Updated token expiry time to " + time);

        String deviceId = "MyJavaDevice";
        double temperature = 0.0;
        double humidity = 0.0;

        for (int i = 0; i < numRequests; ++i) {
            temperature = 20 + Math.random() * 10;
            humidity = 30 + Math.random() * 20;

            String msgStr = "{\"deviceId\":\"" + deviceId + "\",\"messageId\":" + i + ",\"temperature\":" + temperature + ",\"humidity\":" + humidity + "}";

            try {
                Message msg = new Message(msgStr);
                msg.setContentType("application/json");
                msg.setProperty("temperatureAlert", temperature > 28 ? "true" : "false");
                msg.setMessageId(java.util.UUID.randomUUID().toString());
                msg.setExpiryTime(D2C_MESSAGE_TIMEOUT);
                System.out.println(msgStr);
                SendReceive.EventCallback eventCallback = new SendReceive.EventCallback();
                client.sendEventAsync(msg, eventCallback, msg);
            } catch (Exception e) {
                e.printStackTrace(); // Trace the exception
            }

        }

        System.out.println("Wait for " + D2C_MESSAGE_TIMEOUT / 1000 + " second(s) for response from the IoT Hub...");

        // Wait for IoT Hub to respond.
        try {
            Thread.sleep(D2C_MESSAGE_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("In receive mode. Waiting for receiving C2D messages. Press ENTER to close");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // close the connection
        System.out.println("Closing");
        client.closeNow();

        if (!failedMessageListOnClose.isEmpty()) {
            System.out.println("List of messages that were cancelled on close:" + failedMessageListOnClose.toString());
        }

        System.out.println("Shutting down...");
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty() ;
    }
}
