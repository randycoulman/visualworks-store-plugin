package org.jenkinsci.plugins.visualworks_store;

import hudson.util.ArgumentListBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreSCMTest {
    @Test
    public void preparesPollingCommandForSinglePundle() {
        List<PundleSpec> pundles = Arrays.asList(new PundleSpec("Package"));
        StoreSCM scm = new StoreSCM("Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.preparePollingCommand("storeScript");

        assertEquals("storeScript -repository Repo -packages Package -versionRegex \\d+ -blessedAtLeast Development",
                builder.toStringWithQuote());

    }

    @Test
    public void preparesPollingCommandForMultiplePundles() {
        List<PundleSpec> pundles = Arrays.asList(new PundleSpec("Package"), new PundleSpec("OtherPackage"), new PundleSpec("Package with Spaces"));
        StoreSCM scm = new StoreSCM("Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.preparePollingCommand("storeScript");

        final String commandLine = builder.toStringWithQuote();
        assertTrue("Command line (" + commandLine + ") doesn't contain expected text",
                commandLine.contains("-packages Package OtherPackage \"Package with Spaces\""));
    }
}
