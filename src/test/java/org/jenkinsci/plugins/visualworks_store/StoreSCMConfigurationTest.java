package org.jenkinsci.plugins.visualworks_store;

import org.jvnet.hudson.test.HudsonTestCase;

public class StoreSCMConfigurationTest extends HudsonTestCase {
    public void testGlobalConfigurationRoundtrip() throws Exception {
        StoreSCM.DescriptorImpl descriptor = hudson.getDescriptorByType(StoreSCM.DescriptorImpl.class);
        descriptor.setScript("/path/to/storeScript");

        submit(createWebClient().goTo("configure").getFormByName("config"));
        assertEquals("/path/to/storeScript", descriptor.getScript());
    }
}
