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
    public void preparesPollingCommandForSinglePackage() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "Package"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+",
                "Development", false, "");

        ArgumentListBuilder builder = scm.preparePollingCommand("storeScript");

        assertEquals("storeScript -repository Repo -package Package -versionRegex \\d+ -blessedAtLeast Development",
                builder.toStringWithQuote());

    }

    @Test
    public void preparesPollingCommandForSingleBundle() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.BUNDLE, "Bundle"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+",
                "Development", false, "");

        ArgumentListBuilder builder = scm.preparePollingCommand("storeScript");

        assertContains(builder.toStringWithQuote(), "-bundle Bundle");
    }

    @Test
    public void preparesPollingCommandForMultiplePundles() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "Package"),
                new PundleSpec(PundleType.BUNDLE, "Bundle"),
                new PundleSpec(PundleType.PACKAGE, "Package with Spaces"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.preparePollingCommand("storeScript");

        assertContains(builder.toStringWithQuote(),
                "-package Package -bundle Bundle -package \"Package with Spaces\"");
    }

    @Test
    public void preparesCheckoutCommandForOnePackage() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "Package"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("/path/to/changelog.xml"));

        assertEquals("storeScript -repository Repo -package Package -versionRegex \\d+ -blessedAtLeast Development -since \"03/07/2012 15:28:35.000\" -now \"06/15/2012 07:23:42.000\" -changelog /path/to/changelog.xml",
                builder.toStringWithQuote());
    }

    @Test
    public void preparesCheckoutCommandForOneBundle() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.BUNDLE, "Bundle"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("/path/to/changelog.xml"));

        assertContains(builder.toStringWithQuote(), "-bundle Bundle");
    }

    @Test
    public void preparesCheckoutCommandForMultiplePundles() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "Package"),
                new PundleSpec(PundleType.BUNDLE, "Bundle"),
                new PundleSpec(PundleType.PACKAGE, "Package with Spaces"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+", "Development", false, "");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("changelog.xml"));

        assertContains(builder.toStringWithQuote(),
                "-package Package -bundle Bundle -package \"Package with Spaces\"");
    }

    @Test
    public void preparesCheckoutCommandWithParcelBuilderFile() {
        List<PundleSpec> pundles = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "Package"));
        StoreSCM scm = new StoreSCM("Default", "Repo", pundles, "\\d+", "Development", true, "parcelsToBuild");

        ArgumentListBuilder builder = scm.prepareCheckoutCommand("storeScript", lastBuildTime, currentBuildTime, new File("changelog.xml"));

        assertContains(builder.toStringWithQuote(),
                "-parcelBuilderFile parcelsToBuild");
    }

    private void assertContains(String commandLine, String expectedText) {
        assertTrue("{" + commandLine + "} doesn't contain {" +
                expectedText + "}",
                commandLine.contains(expectedText));
    }
}
