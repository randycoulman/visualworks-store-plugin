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

import hudson.model.TaskListener;
import hudson.scm.SCMRevisionState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class StoreRevisionState extends SCMRevisionState {
    private final String repositoryName;
    private Map<String, String> pundleVersions;

    public static StoreRevisionState parse(String repositoryName, String output) {
        StoreRevisionState state = new StoreRevisionState(repositoryName);

        String[] lines = output.split("\\n");
        for (String line : lines) {
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\t");
            state.addPundle(stripQuotes(tokens[1].trim()), tokens[2].trim());
        }
        return state;
    }

    public StoreRevisionState(String repositoryName) {
        this.repositoryName = repositoryName;
        this.pundleVersions = new HashMap<String, String>();
    }

    public void addPundle(String name, String version) {
        pundleVersions.put(name, version);
    }

    public boolean isEmpty() {
        return pundleVersions.isEmpty();
    }

    public int size() {
        return pundleVersions.size();
    }

    public String versionFor(String pundleName) {
        return pundleVersions.get(pundleName);
    }

    public boolean containsPundle(String pundleName) {
        return pundleVersions.containsKey(pundleName);
    }

    public boolean hasChangedFrom(StoreRevisionState other, TaskListener listener) {
        for (Map.Entry<String, String> each : pundleVersions.entrySet()) {
            final String packageName = each.getKey();
            final String newVersion = each.getValue();
            final String otherVersion = other.versionFor(packageName);

            if (otherVersion == null) {
                listener.getLogger().println("New package: " + packageName + " (" + newVersion + ")");
                return true;
            }
            if (otherVersion.equals(newVersion)) continue;

            listener.getLogger().println(packageName + " version changed from \"" + otherVersion + "\" to: \"" + newVersion + "\"");
            return true;
        }
        if (size() == other.size()) return false;

        Set<String> deletedPackages = other.pundlesNotIn(pundleVersions.keySet());
        for (String each : deletedPackages) {
            listener.getLogger().println("Package deleted: " + each);
        }

        return true;
    }

    public void changeVersion(String pundleName, String newVersion) {
        pundleVersions.put(pundleName, newVersion);
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    private Set<String> pundlesNotIn(final Set<String> otherKeys) {
        Set<String> result = new HashSet<String>(pundleVersions.keySet());
        result.removeAll(otherKeys);
        return result;
    }

    private static String stripQuotes(String s) {
        return (s.startsWith("\"")) ? s.substring(1, s.length() - 1) : s;
    }
}
