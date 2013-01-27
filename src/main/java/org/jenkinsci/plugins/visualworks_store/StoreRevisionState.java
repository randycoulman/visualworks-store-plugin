package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.SCMRevisionState;

import java.util.HashMap;
import java.util.Map;

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
}
