package org.jenkinsci.plugins.visualworks_store;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.Iterator;
import java.util.List;

public class StoreChangeLogSet extends ChangeLogSet<StoreChangeLogEntry> {
    private List<StoreChangeLogEntry> entries;

    protected StoreChangeLogSet(AbstractBuild<?, ?> build, List<StoreChangeLogEntry> entries) {
        super(build);
        this.entries = entries;
    }

    @Override
    public String getKind() {
        return "store";
    }

    @Override
    public boolean isEmptySet() {
        return entries.isEmpty();
    }

    public Iterator<StoreChangeLogEntry> iterator() {
        return entries.iterator();
    }

    public List<StoreChangeLogEntry> getEntries() {
        return entries;
    }

    // Synonym for getEntries() - allows this plugin to work with the Multiple SCMs plugin
    @SuppressWarnings("UnusedDeclaration")
    public List<StoreChangeLogEntry> getLogs() {
        return getEntries();
    }
}
