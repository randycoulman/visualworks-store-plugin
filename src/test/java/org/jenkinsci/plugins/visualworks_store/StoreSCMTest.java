package org.jenkinsci.plugins.visualworks_store;

import hudson.util.ArgumentListBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreSCMTest {
    private Calendar lastBuildTime;
    private Calendar currentBuildTime;

    @Before
    public void setUp() {
        lastBuildTime = new GregorianCalendar(2012, 2, 7, 15, 28, 35);
        lastBuildTime.setTimeZone(TimeZone.getTimeZone("GMT"));

        currentBuildTime = new GregorianCalendar(2012, 5, 15, 7, 23, 42);
        currentBuildTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

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

    @Test
    public void preparesCheckoutCommandForOnePundle() {
        List<PundleSpec> pundles = Arrays.asList(new PundleSpec("Package"));
        StoreSCM scm = new StoreSCM("Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("/path/to/changelog.xml"));

        assertEquals("storeScript -repository Repo -packages Package -versionRegex \\d+ -blessedAtLeast Development -since \"03/07/2012 15:28:35.000\" -now \"06/15/2012 07:23:42.000\" -changelog /path/to/changelog.xml",
                builder.toStringWithQuote());
    }

    @Test
    public void preparesCheckoutCommandForMultiplePundles() {
        List<PundleSpec> pundles = Arrays.asList(new PundleSpec("Package"), new PundleSpec("Bundle"), new PundleSpec("Package with Spaces"));
        StoreSCM scm = new StoreSCM("Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("changelog.xml"));

        final String commandLine = builder.toStringWithQuote();
        assertTrue("Command line (" + commandLine + ") doesn't contain expected text",
                commandLine.contains("-packages Package Bundle \"Package with Spaces\""));
    }

    @Test
    public void preparesCheckoutCommandWithParcelBuilderFile() {
        List<PundleSpec> pundles = Arrays.asList(new PundleSpec("Package"));
        StoreSCM scm = new StoreSCM("Repo", pundles, "\\d+", "Development", true, "parcelsToBuild");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("changelog.xml"));

        final String commandLine = builder.toStringWithQuote();
        assertTrue("Command line (" + commandLine + ") doesn't contain expected text",
                commandLine.contains("-parcelBuilderFile parcelsToBuild"));
    }
}
