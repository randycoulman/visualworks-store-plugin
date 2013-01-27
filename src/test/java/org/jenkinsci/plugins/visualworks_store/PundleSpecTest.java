package org.jenkinsci.plugins.visualworks_store;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PundleSpecTest {

    private PundleSpec spec;

    @Before
    public void setUp() throws Exception {
        spec = new PundleSpec("MySpec");
    }

    @Test
    public void isEqualToSelf() {
        assertEquals(spec, spec);
    }

    @Test
    public void isEqualIfNamesMatch() {
        PundleSpec sameName = new PundleSpec("MySpec");

        assertEquals(spec, sameName);
    }

    @Test
    public void notEqualToNull() {
        assertFalse(spec.equals(null));
    }

    @Test
    public void notEqualIfNamesDiffer() {
        PundleSpec differentName = new PundleSpec("differentName");

        assertFalse(spec.equals(differentName));
    }
}
