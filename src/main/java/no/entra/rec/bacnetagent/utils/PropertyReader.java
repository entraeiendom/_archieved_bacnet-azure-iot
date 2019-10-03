package no.entra.rec.bacnetagent.utils;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class PropertyReader {
    private static final Logger log = getLogger(PropertyReader.class);

    public static String findSecretProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
    public static String findProperty(String key) {
        String value = findSecretProperty(key);
        log.debug("Property: {} has value {}", key, value);
        return value;
    }
}
