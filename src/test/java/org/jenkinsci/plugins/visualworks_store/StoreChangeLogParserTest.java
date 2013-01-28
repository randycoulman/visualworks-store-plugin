package org.jenkinsci.plugins.visualworks_store;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StoreChangeLogParserTest {
    @Test
    public void parsesEmptyChangelogFile() throws SAXException, IOException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_empty.xml");
        assertTrue("changeset should be empty", changes.isEmptySet());
    }

    private StoreChangeLogSet parse(String filename) throws IOException, SAXException, URISyntaxException {
        URL url = StoreChangeLogParserTest.class.getResource(filename);
        File changelogFile = new File(url.toURI().getSchemeSpecificPart());

        return new StoreChangeLogParser().parse(null, changelogFile);
    }

    @Test
    public void parsesSingleDeletion() throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_singleDeletion.xml");

        List<StoreChangeLogEntry> entries = changes.getEntries();

        assertEquals("entry count", 1, entries.size());

        StoreChangeLogEntry entry = entries.get(0);
        assertEquals("commit message", "Pundles no longer used", entry.getMsg());
        assertEquals("committer should be empty", "", entry.getCommitter());
        assertEquals("entry timestamp should be zero", 0, entry.getTimestamp());

        assertTrue(entry.getAffectedPaths().contains("MyPundle"));
    }

    @Test
    public void parsesSingleAddition() throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_singleAddition.xml");

        List<StoreChangeLogEntry> entries = changes.getEntries();

        assertEquals("entry count", 1, entries.size());

        StoreChangeLogEntry entry = entries.get(0);
        Calendar expected = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        expected.set(2012, Calendar.JULY, 2, 15, 40, 19);
        expected.set(Calendar.MILLISECOND, 123);

        assertEquals("commit message", "Commit comment.", entry.getMsg());
        assertEquals("committer", "committer", entry.getCommitter());
        assertEquals("timestamp", expected.getTime().getTime(), entry.getTimestamp());

        assertTrue(entry.getAffectedPaths().contains("MyPundle (42)"));
    }

    @Test
    public void parsesMultipleBlessingsAsMultipleEntries() throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_multipleBlessings.xml");
        List<StoreChangeLogEntry> entries = changes.getEntries();

        assertEquals("entry count", 3, entries.size());

        assertEquals("first author", "User 1", entries.get(0).getCommitter());
        assertEquals("second author", "User 2", entries.get(1).getCommitter());
        assertEquals("third author", "User 1", entries.get(2).getCommitter());

        assertEquals("first comment", "First comment.", entries.get(0).getMsg());
        assertEquals("second comment", "Comment the second.", entries.get(1).getMsg());
        assertEquals("third comment", "Other comment.", entries.get(2).getMsg());

        String expectedPath = "MyPundle (42)";
        assertTrue("first paths", entries.get(0).getAffectedPaths().contains(expectedPath));
        assertTrue("second paths", entries.get(1).getAffectedPaths().contains(expectedPath));
        assertTrue("third paths", entries.get(2).getAffectedPaths().contains(expectedPath));
    }

    @Test
    public void parsesMultiplePackagesWithDifferentCommentsAsMultipleEntries() throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_multiplePackages.xml");
        List<StoreChangeLogEntry> entries = changes.getEntries();

        assertEquals("entry count", 3, entries.size());

        assertEquals("first comment", "First comment.", entries.get(0).getMsg());
        assertEquals("second comment", "Pundles no longer used", entries.get(1).getMsg());
        assertEquals("third comment", "Other comment.", entries.get(2).getMsg());

        assertTrue("first paths", entries.get(0).getAffectedPaths().contains("AddedPundle (42)"));
        assertTrue("second paths", entries.get(1).getAffectedPaths().contains("DeletedPundle"));
        assertTrue("third paths", entries.get(2).getAffectedPaths().contains("ModifiedPundle (58)"));
    }

    @Test
    public void mergesEntriesWithIdenticalComments() throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_identicalComments.xml");
        List<StoreChangeLogEntry> entries = changes.getEntries();

        Calendar expectedTimestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        expectedTimestamp.set(2012, Calendar.JULY, 2, 15, 40, 19);
        expectedTimestamp.set(Calendar.MILLISECOND, 123);

        assertEquals("entry count", 3, entries.size());

        assertEquals("first comment", "First comment.", entries.get(0).getMsg());
        assertEquals("second comment", "Pundles no longer used", entries.get(1).getMsg());
        assertEquals("third comment", "Other comment.", entries.get(2).getMsg());

        assertEquals("timestamp of deleted entry", 0, entries.get(1).getTimestamp());
        assertEquals("timestamp should be latest of merged entries", expectedTimestamp.getTime().getTime(), entries.get(2).getTimestamp());

        assertTrue("first paths", entries.get(0).getAffectedPaths().contains("AddedPundle (42)"));

        List<String> expectedPaths1 = Arrays.asList("DeletedPundle1", "DeletedPundle2");
        assertTrue("second paths", entries.get(1).getAffectedPaths().containsAll(expectedPaths1));

        List<String> expectedPaths2 = Arrays.asList("ModifiedPundle1 (58)", "ModifiedPundle2 (123)");
        assertTrue("third paths", entries.get(2).getAffectedPaths().containsAll(expectedPaths2));
    }
}
