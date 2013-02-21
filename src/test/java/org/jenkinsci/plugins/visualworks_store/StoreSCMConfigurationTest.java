package org.jenkinsci.plugins.visualworks_store;

import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

import java.util.Arrays;
import java.util.List;

//@Ignore("Too slow")
public class StoreSCMConfigurationTest extends HudsonTestCase {
    public void testGlobalConfigurationRoundtrip() throws Exception {
        StoreSCM.DescriptorImpl descriptor =
                hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);

        descriptor.setStoreScripts(
                new StoreScript("7.7.1", "/path/to/script-7.7.1"),
                new StoreScript("7.9.1", "/path/to/script-7.9.1"));

        submit(createWebClient().goTo("configure").getFormByName("config"));

        StoreScript[] scripts = descriptor.getStoreScripts();
        assertEquals("installation count", 2, scripts.length);
        assertEquals("first script name", "7.7.1", scripts[0].getName());
        assertEquals("first script path", "/path/to/script-7.7.1",
                scripts[0].getPath());
        assertEquals("second script name", "7.9.1", scripts[1].getName());
        assertEquals("second script path", "/path/to/script-7.9.1",
                scripts[1].getPath());
    }

    public void testBasicConfigurationRoundtrip() throws Exception {
        StoreSCM.DescriptorImpl descriptor =
                hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);
        descriptor.setStoreScripts(new StoreScript("theScript", "path"));

        List<PundleSpec> pundleSpecs = onePundle();
        StoreSCM scm = new StoreSCM("theScript", "Repo", pundleSpecs, "\\d+",
                "Integrated", false, "");
        StoreSCM loaded = doRoundtripConfiguration(scm);

        assertEquals("script name", "theScript", loaded.getScriptName());
        assertEquals("repositoryName", "Repo", loaded.getRepositoryName());
        assertEquals("pundles", pundleSpecs, loaded.getPundles());
        assertEquals("versionRegex", "\\d+", loaded.getVersionRegex());
        assertEquals("minimumBlessingLevel", "Integrated",
                loaded.getMinimumBlessingLevel());
        assertFalse("generateParcelBuilderInputFile",
                loaded.isGenerateParcelBuilderInputFile());
        assertEquals("parcelBuilderInputFilename", "",
                loaded.getParcelBuilderInputFilename());
    }

    public void testConfigurationRoundtripWithMultiplePundles()
            throws Exception {
        StoreSCM.DescriptorImpl descriptor =
                hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);
        descriptor.setStoreScripts(new StoreScript("theScript", "path"));

        List<PundleSpec> pundleSpecs = Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "SomePackage"),
                new PundleSpec(PundleType.BUNDLE, "SomeBundle"));
        StoreSCM scm = new StoreSCM("theScript", "Repo", pundleSpecs, "\\d+",
                "Integrated", false, "");
        StoreSCM loaded = doRoundtripConfiguration(scm);

        assertEquals("pundles", pundleSpecs, loaded.getPundles());
    }

    public void testConfigurationRoundtripWithParcelBuilderFile()
            throws Exception {
        StoreSCM scm = new StoreSCM("script", "Repo", onePundle(), "\\d+",
                "Integrated", true, "theFilename");
        StoreSCM loaded = doRoundtripConfiguration(scm);

        assertTrue("generateParcelBuilderInputFile",
                loaded.isGenerateParcelBuilderInputFile());
        assertEquals("parcelBuilderInputFilename", "theFilename",
                loaded.getParcelBuilderInputFilename());
    }

    public void testLookupStoreScript() {
        StoreSCM.DescriptorImpl descriptor =
                hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);
        final StoreScript script = new StoreScript("otherScript", "otherPath");
        descriptor.setStoreScripts(new StoreScript("theScript", "path"),
                script);

        StoreSCM scm = new StoreSCM("otherScript", "Repo", onePundle(),
                "\\d+", "Development", false, "");

        assertEquals(script, scm.getStoreScript());
    }

    private StoreSCM doRoundtripConfiguration(StoreSCM original) throws Exception {
        FreeStyleProject p = createFreeStyleProject();
        p.setScm(original);

        submit(createWebClient().getPage(p, "configure").getFormByName("config"));

        return (StoreSCM) p.getScm();
    }

    private List<PundleSpec> onePundle() {
        return Arrays.asList(
                new PundleSpec(PundleType.PACKAGE, "SomePackage"));
    }
}
