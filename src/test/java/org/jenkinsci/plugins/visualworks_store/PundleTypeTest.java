package org.jenkinsci.plugins.visualworks_store;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PundleTypeTest {
    @Test
    public void packageDescriptionIsCapitalized() {
        assertEquals("Package", PundleType.PACKAGE.getDescription());
    }

    @Test
    public void packageNameIsLowercase() {
        assertEquals("package", PundleType.PACKAGE.getName());
    }

    @Test
    public void bundleDescriptionIsCapitalized() {
        assertEquals("Bundle", PundleType.BUNDLE.getDescription());
    }

    @Test
    public void bundleNameIsLowercase() {
        assertEquals("bundle", PundleType.BUNDLE.getName());
    }

    @Test
    public void findsPackageTypeByName() {
        assertEquals(PundleType.PACKAGE, PundleType.named("package"));
    }

    @Test
    public void findsBundleTypeByName() {
        assertEquals(PundleType.BUNDLE, PundleType.named("bundle"));
    }

    @Test
    public void returnsNullIfNameNotFound() {
        assertNull(PundleType.named("unknown"));
    }
}
