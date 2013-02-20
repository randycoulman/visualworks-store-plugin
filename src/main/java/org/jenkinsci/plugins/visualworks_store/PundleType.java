package org.jenkinsci.plugins.visualworks_store;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PundleType {
    private String description;

    public static PundleType named(String name) {
        for (PundleType pundleType : ALL) {
            if (pundleType.getName().equals(name)) return pundleType;
        }
        return null;
    }

    public PundleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return description.toLowerCase();
    }

    public static final PundleType PACKAGE = new PundleType("Package");
    public static final PundleType BUNDLE = new PundleType("Bundle");
    private static final List<PundleType> ALL =
            Collections.unmodifiableList(Arrays.asList(PACKAGE, BUNDLE));
}
