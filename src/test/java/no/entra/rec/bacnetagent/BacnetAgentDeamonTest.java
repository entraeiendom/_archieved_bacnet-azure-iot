package no.entra.rec.bacnetagent;

import org.junit.Test;

import static no.entra.rec.bacnetagent.BacnetAgentDeamon.isEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BacnetAgentDeamonTest {

    @Test
    public void testIsEmpty()
    {
        assertTrue(isEmpty(""));
        assertTrue(isEmpty(null));
        assertFalse(isEmpty("hei"));
    }

}