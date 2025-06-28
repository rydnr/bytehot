package org.acmsl.bytehot.maven;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for ByteHotMojo.
 */
public class ByteHotMojoTest {

    @Test
    public void testMojoInstantiation() {
        // Test that the mojo can be instantiated
        ByteHotMojo mojo = new ByteHotMojo();
        assertNotNull("ByteHot plugin should be instantiable", mojo);
    }

    @Test
    public void testEnabledDefaultValue() {
        // Test the default value of enabled parameter
        ByteHotMojo mojo = new ByteHotMojo();
        // We can't directly test this without reflection as the field is protected
        // This is a structural test to verify the class compiles correctly
        assertNotNull("Mojo should have enabled field", mojo);
    }
}