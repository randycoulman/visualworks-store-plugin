package org.jenkinsci.plugins.visualworks_store;

import org.kohsuke.stapler.DataBoundConstructor;

public final class StoreScript {
    private final String name;
    private final String path;

    @DataBoundConstructor
    public StoreScript(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
