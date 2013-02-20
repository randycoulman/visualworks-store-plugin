package org.jenkinsci.plugins.visualworks_store;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class PundleSpec extends AbstractDescribableImpl<PundleSpec> {
    private String name;
    private PundleType pundleType = PundleType.PACKAGE;

    @DataBoundConstructor
    public PundleSpec(PundleType pundleType, String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException();

        this.pundleType = pundleType == null ? PundleType.PACKAGE : pundleType;
        this.name = name;
    }

    @DataBoundConstructor
    public PundleSpec(String name) {
        this(PundleType.PACKAGE, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PundleSpec that = (PundleSpec) o;

        return pundleType.equals(that.pundleType) && name.equals(that.name);
    }

    @Override
    public String toString() {
        return "PundleSpec{" + pundleType.getName() + " " + name + "}";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public PundleType getPundleType() {
        return pundleType;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PundleSpec> {
        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
