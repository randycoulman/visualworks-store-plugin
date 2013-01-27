package org.jenkinsci.plugins.visualworks_store;

import org.junit.Test;

import static org.junit.Assert.*;

public class StoreRevisionStateTest {
    @Test
    public void parsesEmptyInput() {
        StoreRevisionState state = StoreRevisionState.parse("");
        assertTrue("should be empty", state.isEmpty());
    }

    @Test
    public void parsesSinglePackageLine() {
        StoreRevisionState state = StoreRevisionState.parse("StorePackage\t\"MyPackage\"\tsome version");
        assertEquals("package count", 1, state.size());
        assertEquals("version", "some version", state.versionFor("MyPackage"));
    }

    @Test
    public void parsesMultiplePackageLines() {
        String input = "StorePackage\t\"PackageA\"\tversion A\nStorePackage\t\"PackageB\"\tversion B";

        StoreRevisionState state = StoreRevisionState.parse(input);

        assertEquals("package count", 2, state.size());
        assertEquals("PackageA version", "version A", state.versionFor("PackageA"));
        assertEquals("PackageB version", "version B", state.versionFor("PackageB"));
    }

    @Test
    public void ignoresExtraWhitespaceWhenParsing() {
        String input = "           StorePackage       \t                \"PackageA\"      \t       version A      \n\n\n\n     StorePackage     \t  \"PackageB\"   \t     version B     ";

        StoreRevisionState state = StoreRevisionState.parse(input);

        assertEquals("package count", 2, state.size());
        assertEquals("PackageA version", "version A", state.versionFor("PackageA"));
        assertEquals("PackageB version", "version B", state.versionFor("PackageB"));
    }

    @Test
    public void parsesPundleNamesWithEmbeddedSpaces() {
        StoreRevisionState state = StoreRevisionState.parse("StorePackage\t\"Package With Spaces\"\tsome version");
        assertTrue("package not found", state.containsPundle("Package With Spaces"));
    }

    @Test
    public void parsesPundleNamesWithNoSurroundingQuotes() {
        StoreRevisionState state = StoreRevisionState.parse("StorePackage\tMyPackage\tsome version");
        assertTrue("package not found", state.containsPundle("MyPackage"));
    }

    @Test
    public void returnsNullVersionIfPackageNotFound() {
        StoreRevisionState state = new StoreRevisionState();

        assertNull("should have return null version", state.versionFor("Not There"));
    }
}
