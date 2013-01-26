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

    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject p = createFreeStyleProject();
        StoreSCM original = new StoreSCM("Repo", "\\d+");
        p.setScm(original);

        submit(createWebClient().getPage(p, "configure").getFormByName("config"));

        StoreSCM loaded = (StoreSCM) p.getScm();
        assertEquals("repositoryName", original.getRepositoryName(), loaded.getRepositoryName());
        assertEquals("versionRegex", original.getVersionRegex(), loaded.getVersionRegex());
    }

}
