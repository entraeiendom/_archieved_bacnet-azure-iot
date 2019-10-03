package no.entra.rec.bacnetagent;
// Based on example by:
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

// This application uses the Microsoft Azure Event Hubs Client for Java
// For samples see: https://github.com/Azure/azure-event-hubs/tree/master/samples/Java
// For documenation see: https://docs.microsoft.com/azure/event-hubs/

import com.microsoft.azure.eventhubs.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class ReadDeviceToCloudMessages {
    private static final Logger log = getLogger(ReadDeviceToCloudMessages.class);

    //TODO #3 Read IOT_SERVICE_PRIMARY_KEY, EVENT_HUB_PATH and EVENT_HUB_ENDPOINT
    // az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
    private static final String eventHubsCompatibleEndpoint = "{your Event Hubs compatible endpoint}";

    // az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
    private static final String eventHubsCompatiblePath = "{your Event Hubs compatible name}";

    // az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
    private static final String iotHubSasKey = "{your service primary key}";
    private static final String iotHubSasKeyName = "service";

    // Track all the PartitionReciever instances created.
    private static ArrayList<PartitionReceiver> receivers = new ArrayList<PartitionReceiver>();

    // Asynchronously create a PartitionReceiver for a partition and then start
    // reading any messages sent from the simulated client.
    private static void receiveMessages(EventHubClient ehClient, String partitionId)
            throws EventHubException, ExecutionException, InterruptedException {

        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Create the receiver using the default consumer group.
        // For the purposes of this sample, read only messages sent since
        // the time the receiver is created. Typically, you don't want to skip any messages.
        ehClient.createReceiver(EventHubClient.DEFAULT_CONSUMER_GROUP_NAME, partitionId,
                EventPosition.fromEnqueuedTime(Instant.now())).thenAcceptAsync(receiver -> {
            log.debug(String.format("Starting receive loop on partition: %s", partitionId));
            log.debug(String.format("Reading messages sent since: %s", Instant.now().toString()));

            receivers.add(receiver);

            while (true) {
                try {
                    // Check for EventData - this methods times out if there is nothing to retrieve.
                    Iterable<EventData> receivedEvents = receiver.receiveSync(100);

                    // If there is data in the batch, process it.
                    if (receivedEvents != null) {
                        for (EventData receivedEvent : receivedEvents) {
                            log.debug(String.format("Telemetry received:\n %s",
                                    new String(receivedEvent.getBytes(), Charset.defaultCharset())));
                            log.debug(String.format("Application properties (set by device):\n%s",receivedEvent.getProperties().toString()));
                            log.debug(String.format("System properties (set by IoT Hub):\n%s\n",receivedEvent.getSystemProperties().toString()));
                        }
                    }
                } catch (EventHubException e) {
                    log.debug("Error reading EventData");
                }
            }
        }, executorService);
    }

    public static void main(String[] args)
            throws EventHubException, ExecutionException, InterruptedException, IOException, URISyntaxException {

        final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
                .setEndpoint(new URI(eventHubsCompatibleEndpoint))
                .setEventHubName(eventHubsCompatiblePath)
                .setSasKeyName(iotHubSasKeyName)
                .setSasKey(iotHubSasKey);

        /*
        String deviceConnectionString = System.getenv("DEVICE_CONNECTION_STRING");
        if (deviceConnectionString == null) {
            if (args.length > 0) {
                deviceConnectionString = args[0];
            }
        }

        if (isEmpty(deviceConnectionString)) {
            log.error("Missing required property: DEVICE_CONNECTION_STRING, exiting ");
            System.exit(1);
        }
        */

        // Create an EventHubClient instance to connect to the
        // IoT Hub Event Hubs-compatible endpoint.
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executorService);

        // Use the EventHubRunTimeInformation to find out how many partitions
        // there are on the hub.
        final EventHubRuntimeInformation eventHubInfo = ehClient.getRuntimeInformation().get();

        // Create a PartitionReciever for each partition on the hub.
        for (String partitionId : eventHubInfo.getPartitionIds()) {
            receiveMessages(ehClient, partitionId);
        }

        // Shut down cleanly.
        log.debug("Press ENTER to exit.");
        System.in.read();
        log.debug("Shutting down...");
        for (PartitionReceiver receiver : receivers) {
            receiver.closeSync();
        }
        ehClient.closeSync();
        executorService.shutdown();
        System.exit(0);
    }
}