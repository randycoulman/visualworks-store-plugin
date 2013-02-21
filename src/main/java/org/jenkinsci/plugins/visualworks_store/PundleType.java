package org.jenkinsci.plugins.visualworks_store;

public enum PundleType {
    PACKAGE("Package"),
    BUNDLE("Bundle");

    private final String description;

    public static PundleType named(String name) {
        for (PundleType pundleType : values()) {
            if (pundleType.getName().equals(name)) return pundleType;
        }
        return null;
    }

    private PundleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return description.toLowerCase();
    }
}
