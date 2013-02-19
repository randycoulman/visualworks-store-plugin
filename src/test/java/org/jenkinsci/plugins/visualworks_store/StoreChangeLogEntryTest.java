package org.jenkinsci.plugins.visualworks_store;

import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreChangeLogEntryTest {
    private StoreChangeLogEntry entry;
    private String timestampString = "07/02/2012 15:40:19.123";
    private String laterTimestampString = "07/02/2012 15:45:21.456";
    private ChangedPundle addedPundle;
    private ChangedPundle editedPundle;
    private ChangedPundle deletedPundle;

    @Before
    public void setUp() {
        entry = new StoreChangeLogEntry("committer", timestampString, "blessing comment");
        addedPundle = new ChangedPundle("added", "AddedPundle", "1");
        editedPundle = new ChangedPundle("edited", "EditedPundle", "42");
        deletedPundle = new ChangedPundle("deleted", "DeletedPundle");
    }

    @Test
    public void remembersCommitter() {
        assertEquals("committer", entry.getCommitter());
    }

    @Test
    public void parsesTimestampStrings() throws ParseException {
        Calendar expected = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        expected.set(2012, Calendar.JULY, 2, 15, 40, 19);
        expected.set(Calendar.MILLISECOND, 123);

        assertEquals(expected.getTime().getTime(), entry.getTimestamp());
    }

    @Test
    public void updatesTimestampToNewerOne() {
        long originalTimestamp = entry.getTimestamp();

        entry.updateTimestamp(laterTimestampString);

        assertTrue("timestamp should have been updated", entry.getTimestamp() > originalTimestamp);
    }

    @Test
    public void doesntUpdateTimestampWithOlderOne() {
        entry.updateTimestamp(laterTimestampString);

        long originalTimestamp = entry.getTimestamp();

        entry.updateTimestamp(timestampString);

        assertEquals("timestamp should not have been updated", originalTimestamp, entry.getTimestamp());
    }

    @Test
    public void returnsBlessingCommentAsCommitMessage() {
        assertEquals("blessing comment", entry.getMsg());
    }

    @Test
    public void splitsMultilineBlessingComments() {
        entry = new StoreChangeLogEntry("committer", timestampString, "Comment title.\nDetails 1.\nDetails 2.");

        assertEquals("message", "Comment title.", entry.getMsg());
        assertEquals("full comment", "Comment title.\nDetails 1.\nDetails 2.", entry.getFullComment());
    }

    @Test
    public void returnsEmptyAffectedPathsIfNoPundles() {
        assertTrue("shouldn't have any affected paths", entry.getAffectedPaths().isEmpty());
    }

    @Test
    public void returnsPundleDescriptorsAsAffectedPaths() {
        entry.addPundle(addedPundle);
        entry.addPundle(editedPundle);
        entry.addPundle(deletedPundle);

        List<String> expected = Arrays.asList(addedPundle.getDescriptor(),
                editedPundle.getDescriptor(), deletedPundle.getDescriptor());
        assertTrue(entry.getAffectedPaths().containsAll(expected));
    }

    @Test
    public void returnsAffectedFiles() {
        entry.addPundle(addedPundle);
        entry.addPundle(editedPundle);

        final Collection<? extends ChangeLogSet.AffectedFile> affectedFiles = entry.getAffectedFiles();
        ChangeLogSet.AffectedFile[] files = new ChangeLogSet.AffectedFile[affectedFiles.size()];
        affectedFiles.toArray(files);

        assertEquals(2, files.length);
        ChangeLogSet.AffectedFile file1 = files[0];
        assertEquals(addedPundle.getDescriptor(), file1.getPath());
        assertEquals(EditType.ADD, file1.getEditType());

        ChangeLogSet.AffectedFile file2 = files[1];
        assertEquals(editedPundle.getDescriptor(), file2.getPath());
        assertEquals(EditType.EDIT, file2.getEditType());
    }
}
