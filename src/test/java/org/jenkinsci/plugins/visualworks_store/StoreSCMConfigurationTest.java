package org.jenkinsci.plugins.visualworks_store;

import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

public class StoreSCMConfigurationTest extends HudsonTestCase {
    public void testGlobalConfigurationRoundtrip() throws Exception {
        StoreSCM.DescriptorImpl descriptor = hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);
        descriptor.setScript("/path/to/storeScript");

        submit(createWebClient().goTo("configure").getFormByName("config"));
        assertEquals("/path/to/storeScript", descriptor.getScript());
    }

    public void testBasicConfigurationRoundtrip() throws Exception {
        StoreSCM scm = new StoreSCM("Repo", "\\d+", "Integrated", false, "");
        StoreSCM loaded = doRoundtripConfiguration(scm);

        assertEquals("repositoryName", "Repo", loaded.getRepositoryName());
        assertEquals("versionRegex", "\\d+", loaded.getVersionRegex());
        assertEquals("minimumBlessingLevel", "Integrated", loaded.getMinimumBlessingLevel());
        assertFalse("generateParcelBuilderInputFile", loaded.isGenerateParcelBuilderInputFile());
        assertEquals("parcelBuilderInputFilename", "", loaded.getParcelBuilderInputFilename());
    }

    public void testConfigurationRoundtripWithParcelBuilderFile() throws Exception {
        StoreSCM scm = new StoreSCM("Repo", "\\d+", "Integrated", true, "theFilename");
        StoreSCM loaded = doRoundtripConfiguration(scm);

        assertTrue("generateParcelBuilderInputFile", loaded.isGenerateParcelBuilderInputFile());
        assertEquals("parcelBuilderInputFilename", "theFilename", loaded.getParcelBuilderInputFilename());
    }

    private StoreSCM doRoundtripConfiguration(StoreSCM original) throws Exception {
        FreeStyleProject p = createFreeStyleProject();
        p.setScm(original);

        submit(createWebClient().getPage(p, "configure").getFormByName("config"));

        return (StoreSCM) p.getScm();
    }

}
