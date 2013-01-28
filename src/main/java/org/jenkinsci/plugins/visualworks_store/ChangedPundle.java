package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;

public class ChangedPundle implements ChangeLogSet.AffectedFile {
    private EditType editType;
    private String name;
    private String version;

    public ChangedPundle(String action, String name, String version) {
        this.editType = toEditType(action);
        this.name = name;
        this.version = version;
    }

    public ChangedPundle(String action, String name) {
        this(action, name, null);
    }

    private EditType toEditType(String action) {
        if (action.equals("added")) return EditType.ADD;
        if (action.equals("deleted")) return EditType.DELETE;

        return EditType.EDIT;
    }

    public String getPath() {
        return getDescriptor();
    }

    public EditType getEditType() {
        return editType;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescriptor() {
        if (isDeletion()) return name;

        return name + " (" + version + ")";
    }

    private boolean isDeletion() {
        return editType == EditType.DELETE;
    }
}
