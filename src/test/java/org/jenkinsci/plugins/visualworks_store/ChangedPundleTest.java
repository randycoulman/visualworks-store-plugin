package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.EditType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChangedPundleTest {

    @Test
    public void convertsAddedActionToEditTypeADD() {
        ChangedPundle pundle = new ChangedPundle("added", "pundleName", "42");

        assertEquals("pundleName", pundle.getName());
        assertEquals("42", pundle.getVersion());
        assertEquals(EditType.ADD, pundle.getEditType());
    }

    @Test
    public void convertsDeletedActionToEditTypeDELETE() {
        ChangedPundle pundle = new ChangedPundle("deleted", "pundleName");

        assertEquals("pundleName", pundle.getName());
        assertNull("version should be null", pundle.getVersion());
        assertEquals(EditType.DELETE, pundle.getEditType());
    }

    @Test
    public void convertsModifiedActionToEditTypeEDIT() {
        ChangedPundle pundle = new ChangedPundle("edited", "pundleName", "42");

        assertEquals("pundleName", pundle.getName());
        assertEquals("42", pundle.getVersion());
        assertEquals(EditType.EDIT, pundle.getEditType());
    }

    @Test
    public void constructsPundleDescriptor() {
        ChangedPundle pundle = new ChangedPundle("edited", "pundleName", "42");

        assertEquals("pundleName (42)", pundle.getDescriptor());
    }

    @Test
    public void omitsVersionFromPundleDescriptorOnDeletedPundle() {
        ChangedPundle pundle = new ChangedPundle("deleted", "pundleName");

        assertEquals("pundleName", pundle.getDescriptor());
    }

    @Test
    public void usesDescriptorAsPath() {
        ChangedPundle pundle = new ChangedPundle("edited", "pundleName", "42");

        assertEquals("pundleName (42)", pundle.getPath());
    }
}
