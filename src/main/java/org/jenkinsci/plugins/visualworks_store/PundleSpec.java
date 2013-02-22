/*
 * The MIT License
 *
 * Copyright (c) 2013. Randy Coulman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PundleSpec that = (PundleSpec) o;

        return pundleType.equals(that.pundleType) && name.equals(that.name);
    }

    @Override
    public String toString() {
        return "PundleSpec{" + pundleType.getDescription() + " " + name + "}";
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
