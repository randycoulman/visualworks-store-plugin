/*
 * The MIT License
 *
 * Copyright (c) 2013. Randy Coulman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.visualworks_store;

import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class StoreRevisionStateTest {
    private ByteArrayOutputStream logOutput;
    private TaskListener listener;

    @Before
    public void configureListener() {
        logOutput = new ByteArrayOutputStream();
        listener = new StreamTaskListener(logOutput);
    }

    @Test
    public void preservesRepositoryName() {
        StoreRevisionState state = StoreRevisionState.parse("repo", "");
        assertEquals("repositoryName", "repo", state.getRepositoryName());
    }

    @Test
    public void parsesEmptyInput() {
        StoreRevisionState state = StoreRevisionState.parse("repo", "");
        assertTrue("should be empty", state.isEmpty());
    }

    @Test
    public void parsesSinglePackageLine() {
        StoreRevisionState state = StoreRevisionState.parse("repo", "StorePackage\t\"MyPackage\"\tsome version");
        assertEquals("package count", 1, state.size());
        assertEquals("version", "some version", state.versionFor("MyPackage"));
    }

    @Test
    public void parsesMultiplePackageLines() {
        String input = "StorePackage\t\"PackageA\"\tversion A\nStorePackage\t\"PackageB\"\tversion B";

        StoreRevisionState state = StoreRevisionState.parse("repo", input);

        assertEquals("package count", 2, state.size());
        assertEquals("PackageA version", "version A", state.versionFor("PackageA"));
        assertEquals("PackageB version", "version B", state.versionFor("PackageB"));
    }

    @Test
    public void ignoresExtraWhitespaceWhenParsing() {
        String input = "           StorePackage       \t                \"PackageA\"      \t       version A      \n\n\n\n     StorePackage     \t  \"PackageB\"   \t     version B     ";

        StoreRevisionState state = StoreRevisionState.parse("repo", input);

        assertEquals("package count", 2, state.size());
        assertEquals("PackageA version", "version A", state.versionFor("PackageA"));
        assertEquals("PackageB version", "version B", state.versionFor("PackageB"));
    }

    @Test
    public void parsesPundleNamesWithEmbeddedSpaces() {
        StoreRevisionState state = StoreRevisionState.parse("repo", "StorePackage\t\"Package With Spaces\"\tsome version");
        assertTrue("package not found", state.containsPundle("Package With Spaces"));
    }

    @Test
    public void parsesPundleNamesWithNoSurroundingQuotes() {
        StoreRevisionState state = StoreRevisionState.parse("repo", "StorePackage\tMyPackage\tsome version");
        assertTrue("package not found", state.containsPundle("MyPackage"));
    }

    // TODO: Tests to write:
    // syntax errors: not enough tokens; too many tokens;
    // missing quotes;

    @Test
    public void returnsNullVersionIfPackageNotFound() {
        StoreRevisionState state = new StoreRevisionState("repo");

        assertNull("should have return null version", state.versionFor("Not There"));
    }

    @Test
    public void hasntChangedWithSameStateObject() {
        StoreRevisionState baseline = makeBaselineState();

        assertFalse("shouldn't have changes", baseline.hasChangedFrom(baseline, listener));
        assertLogEmpty();
    }

    @Test
    public void hasntChangedWithCopyOfSameState() {
        StoreRevisionState baseline = makeBaselineState();
        StoreRevisionState copy = makeBaselineState();

        assertFalse("shouldn't have changes", copy.hasChangedFrom(baseline, listener));
        assertLogEmpty();
    }

    @Test
    public void hasChangedWithDifferentVersion() {
        StoreRevisionState baseline = makeBaselineState();
        StoreRevisionState differentVersion = makeBaselineState();
        differentVersion.changeVersion("Package 2", "different");

        assertTrue("should have changed", differentVersion.hasChangedFrom(baseline, listener));
        assertLogContains("Package 2 version changed from \"2\" to: \"different\"\n");
    }

    @Test
    public void hasChangedWithExtraPackage() {
        StoreRevisionState baseline = makeBaselineState();
        StoreRevisionState extraPackage = makeBaselineState();
        extraPackage.addPundle("Extra", "2.0");

        assertTrue("should have changed", extraPackage.hasChangedFrom(baseline, listener));
        assertLogContains("New package: Extra (2.0)\n");
    }

    @Test
    public void hasChangedWithMissingPackage() {
        StoreRevisionState baseline = makeBaselineState();
        StoreRevisionState missingPackage = new StoreRevisionState("repo");

        assertTrue("should have changed", missingPackage.hasChangedFrom(baseline, listener));
        assertLogContains("Package deleted: Package 1\n");
        assertLogContains("Package deleted: Package 2\n");
    }

    private StoreRevisionState makeBaselineState() {
        StoreRevisionState state = new StoreRevisionState("repo");
        state.addPundle("Package 1", "1");
        state.addPundle("Package 2", "2");
        return state;
    }

    private void assertLogEmpty() {
        assertTrue("log should be empty", logOutput.toString().isEmpty());
    }

    private void assertLogContains(final String snippet) {
        final String logString = logOutput.toString();
        assertTrue(logString + "\n\tdoes not contain\n" + snippet, logString.contains(snippet));
    }
}
