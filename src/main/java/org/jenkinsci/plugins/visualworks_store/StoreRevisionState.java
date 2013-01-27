package org.jenkinsci.plugins.visualworks_store;

import hudson.model.TaskListener;
import hudson.scm.SCMRevisionState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class StoreRevisionState extends SCMRevisionState {
    private Map<String, String> pundleVersions = new HashMap<String, String>();

    public static StoreRevisionState parse(String output) {
        StoreRevisionState state = new StoreRevisionState();

        String[] lines = output.split("\\n");
        for (String line : lines) {
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\t");
            state.addPundle(stripQuotes(tokens[1].trim()), tokens[2].trim());
        }
        return state;
    }

    private static String stripQuotes(String s) {
        return (s.startsWith("\"")) ? s.substring(1, s.length() - 1) : s;
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

    private Set<String> pundlesNotIn(final Set<String> otherKeys) {
        Set<String> result = new HashSet<String>(pundleVersions.keySet());
        result.removeAll(otherKeys);
        return result;
    }

    public void changeVersion(String pundleName, String newVersion) {
        pundleVersions.put(pundleName, newVersion);
    }
}
