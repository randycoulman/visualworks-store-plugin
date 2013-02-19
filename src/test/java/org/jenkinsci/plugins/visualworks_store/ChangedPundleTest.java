package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.EditType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChangedPundleTest {

    @Test
    public void convertsAddedActionToEditTypeADD() {
        ChangedPundle pundle = new ChangedPundle("added", PundleType.PACKAGE, "pundleName", "42");

        assertEquals(EditType.ADD, pundle.getEditType());
    }

    @Test
    public void convertsDeletedActionToEditTypeDELETE() {
        ChangedPundle pundle = new ChangedPundle("deleted", PundleType.PACKAGE, "pundleName");

        assertEquals(EditType.DELETE, pundle.getEditType());
    }

    @Test
    public void convertsModifiedActionToEditTypeEDIT() {
        ChangedPundle pundle = new ChangedPundle("edited", PundleType.PACKAGE, "pundleName", "42");

        assertEquals(EditType.EDIT, pundle.getEditType());
    }

    @Test
    public void constructsPundleDescriptor() {
        ChangedPundle pundle = new ChangedPundle("edited", PundleType.PACKAGE, "pundleName", "42");

        assertEquals("Package pundleName (42)", pundle.getDescriptor());
    }

    @Test
    public void omitsVersionFromPundleDescriptorOnDeletedPundle() {
        ChangedPundle pundle = new ChangedPundle("deleted", PundleType.BUNDLE,
                "pundleName");

        assertEquals("Bundle pundleName", pundle.getDescriptor());
    }

    @Test
    public void usesDescriptorAsPath() {
        ChangedPundle pundle = new ChangedPundle("edited", PundleType.PACKAGE, "pundleName", "42");

        assertEquals("Package pundleName (42)", pundle.getPath());
    }
}
