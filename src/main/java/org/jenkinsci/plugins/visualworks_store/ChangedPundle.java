package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;

public class ChangedPundle implements ChangeLogSet.AffectedFile {
    private EditType editType;
    private PundleType pundleType;
    private String name;
    private String version;

    public ChangedPundle(String action, PundleType pundleType, String name, String version) {
        this.editType = toEditType(action);
        this.pundleType = pundleType;
        this.name = name;
        this.version = version;
    }

    public ChangedPundle(String action, PundleType pundleType, String name) {
        this(action, pundleType, name, null);
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

    public String getDescriptor() {
        String descriptor = pundleType.getName() + " " + name;
        if (isDeletion()) return descriptor;

        return descriptor + " (" + version + ")";
    }

    private boolean isDeletion() {
        return editType == EditType.DELETE;
    }
}
