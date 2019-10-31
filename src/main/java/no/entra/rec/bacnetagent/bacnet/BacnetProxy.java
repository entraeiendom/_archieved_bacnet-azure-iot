package no.entra.rec.bacnetagent.bacnet;

import org.code_house.bacnet4j.wrapper.api.BacNetToJavaConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class BacnetProxy {
    private static final Logger log = getLogger(BacnetProxy.class);
    private static final int BACNET_AGENT_DEVICE_ID = 2001;
    private final BacNetIpClient client;

    public BacnetProxy(BacNetIpClient client) {
        this.client = client;
    }

    public BacnetProxy(InetAddress bacnetAgentAddress) {
        client = new BacNetIpClient(bacnetAgentAddress.getHostAddress(), "255.255.255.255", BACNET_AGENT_DEVICE_ID);

    }

    public void discoverAndReport() {
        client.start();
        Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
        log.info("Found devices: " + devices.size());
        for (Device device : devices) {
            log.info("Device: {}", device);

            for (Property property : client.getDeviceProperties(device)) {
                BacNetToJavaConverter<String> converter = new StringBacNetToJavaConverter();
                try {
                    log.info(property.getName() + " " + client.getPropertyValue(property, converter));
                } catch (BacNetUnknownPropertyException e) {
                    log.info("Property could not be read. Name: {}, Property: {}. ", property.getName(), property);
                }
            }
        }

        client.stop();

    }

    public static void main(String[] args) throws UnknownHostException {
        int clientDeviceId = 2001;
        //BacNetIpClient client = new BacNetIpClient("192.168.1.31", "255.255.255.255", clientDeviceId);
        //BacnetProxy proxy = new BacnetProxy(client);
        InetAddress bacnetAgentAddress = InetAddress.getByName("192.168.1.31");
        BacnetProxy proxy = new BacnetProxy(bacnetAgentAddress);
        proxy.discoverAndReport();

    }

    private static class StringBacNetToJavaConverter implements BacNetToJavaConverter<String> {
        @Override
        public String fromBacNet(com.serotonin.bacnet4j.type.Encodable encodable) {
            return encodable.toString();
        }
    }
}
