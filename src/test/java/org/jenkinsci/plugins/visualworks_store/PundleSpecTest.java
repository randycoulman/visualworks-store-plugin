package org.jenkinsci.plugins.visualworks_store;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PundleSpecTest {

    private PundleSpec spec;

    @Before
    public void setUp() throws Exception {
        spec = new PundleSpec(PundleType.PACKAGE, "MySpec");
    }

    @Test
    public void isEqualToSelf() {
        assertEquals(spec, spec);
    }

    @Test
    public void isEqualIfTypesAndNamesMatch() {
        PundleSpec sameName = new PundleSpec(PundleType.PACKAGE, "MySpec");

        assertEquals(spec, sameName);
    }

    @Test
    public void notEqualToNull() {
        assertFalse(spec.equals(null));
    }

    @Test
    public void notEqualIfNamesDiffer() {
        PundleSpec differentName =
                new PundleSpec(PundleType.PACKAGE, "differentName");

        assertFalse(spec.equals(differentName));
    }

    @Test
    public void notEqualIfTypesDiffer() {
        PundleSpec differentType =
                new PundleSpec(PundleType.BUNDLE, "MySpec");

        assertFalse(spec.equals(differentType));
    }
}
