package org.jenkinsci.plugins.visualworks_store;

public class PundleType {
    private String name;

    public PundleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static final PundleType PACKAGE = new PundleType("Package");
    public static final PundleType BUNDLE = new PundleType("Bundle");
}
