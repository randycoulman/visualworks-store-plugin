package org.jenkinsci.plugins.visualworks_store;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

class PundleSpec extends AbstractDescribableImpl<PundleSpec> {
    private String name;

    @DataBoundConstructor
    public PundleSpec(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException();

        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PundleSpec that = (PundleSpec) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PundleSpec> {
        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
