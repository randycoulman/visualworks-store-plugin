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

        ChangedPundle pundle = new ChangedPundle("deleted",
                PundleType.PACKAGE, "MyPundle");
        assertTrue(entry.getAffectedPaths().contains(pundle.getDescriptor()));
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

        ChangedPundle pundle = new ChangedPundle("added",
                PundleType.PACKAGE, "MyPundle", "42");
        assertTrue(entry.getAffectedPaths().contains(pundle.getDescriptor()));
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

        ChangedPundle pundle = new ChangedPundle("edited",
                PundleType.PACKAGE, "MyPundle", "42");
        String expectedPath = pundle.getDescriptor();
        assertTrue("first paths", entries.get(0).getAffectedPaths().contains(expectedPath));
        assertTrue("second paths", entries.get(1).getAffectedPaths().contains(expectedPath));
        assertTrue("third paths", entries.get(2).getAffectedPaths().contains(expectedPath));
    }

    @Test
    public void parsesMultiplePundlesWithDifferentCommentsAsMultipleEntries()
            throws IOException, SAXException, URISyntaxException {
        StoreChangeLogSet changes = parse("changelog_multiplePundles.xml");
        List<StoreChangeLogEntry> entries = changes.getEntries();

        assertEquals("entry count", 3, entries.size());

        assertEquals("first comment", "First comment.", entries.get(0).getMsg());
        assertEquals("second comment", "Pundles no longer used", entries.get(1).getMsg());
        assertEquals("third comment", "Other comment.", entries.get(2).getMsg());

        ChangedPundle addedPundle = new ChangedPundle("added",
                PundleType.PACKAGE, "AddedPundle", "42");
        ChangedPundle deletedPundle = new ChangedPundle("deleted",
                PundleType.PACKAGE, "DeletedPundle");
        ChangedPundle modifiedPundle = new ChangedPundle("edited",
                PundleType.BUNDLE, "ModifiedPundle", "58");

        assertTrue("first paths", entries.get(0).getAffectedPaths().contains
                (addedPundle.getDescriptor()));
        assertTrue("second paths", entries.get(1).getAffectedPaths().contains
                (deletedPundle.getDescriptor()));
        assertTrue("third paths", entries.get(2).getAffectedPaths().contains
                (modifiedPundle.getDescriptor()));
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

        ChangedPundle addedPundle = new ChangedPundle("added",
                PundleType.PACKAGE, "AddedPundle", "42");
        ChangedPundle deletedPundle1 = new ChangedPundle("deleted",
                PundleType.BUNDLE, "DeletedPundle1");
        ChangedPundle deletedPundle2 = new ChangedPundle("deleted",
                PundleType.PACKAGE, "DeletedPundle2");
        ChangedPundle modifiedPundle1 = new ChangedPundle("edited",
                PundleType.BUNDLE, "ModifiedPundle1", "58");
        ChangedPundle modifiedPundle2 = new ChangedPundle("edited",
                PundleType.PACKAGE, "ModifiedPundle2", "123");

        assertTrue("first paths", entries.get(0).getAffectedPaths().contains
                (addedPundle.getDescriptor()));

        List<String> expectedPaths1 = Arrays.asList(deletedPundle1.getDescriptor(),
                deletedPundle2.getDescriptor());
        assertTrue("second paths", entries.get(1).getAffectedPaths().containsAll(expectedPaths1));

        List<String> expectedPaths2 =
                Arrays.asList(modifiedPundle1.getDescriptor(),
                        modifiedPundle2.getDescriptor());
        assertTrue("third paths", entries.get(2).getAffectedPaths().containsAll(expectedPaths2));
    }
}
