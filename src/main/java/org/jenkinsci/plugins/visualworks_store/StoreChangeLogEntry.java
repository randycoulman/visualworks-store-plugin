package org.jenkinsci.plugins.visualworks_store;

import hudson.MarkupText;
import hudson.model.User;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StoreChangeLogEntry extends ChangeLogSet.Entry {
    private String committer = "";
    private Date timestamp;
    private String blessingTitle = "";
    private String blessingComment = "";
    private List<ChangedPundle> pundles = new ArrayList<ChangedPundle>();

    public StoreChangeLogEntry(String committer, String timestampString, String blessingComment) {
        this.committer = committer;
        this.timestamp = parseTimestamp(timestampString);
        this.blessingComment = blessingComment;

        int index = blessingComment.indexOf('\n');
        this.blessingTitle = (index < 0) ? blessingComment : blessingComment.substring(0, index);
    }

    @Override
    public String getMsg() {
        return blessingTitle;
    }

    public String getFullComment() {
        return blessingComment;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getFullCommentAnnotated() {
        MarkupText markup = new MarkupText(getFullComment());
        for (ChangeLogAnnotator a : ChangeLogAnnotator.all()) {
            a.annotate(getParent().build, this, markup);
        }

        return markup.toString(false);
    }

    @Override
    public User getAuthor() {
        return committer.isEmpty() ? User.getUnknown() : User.get(committer);
    }

    @Override
    public Collection<String> getAffectedPaths() {
        final ArrayList<String> paths = new ArrayList<String>();
        for (ChangedPundle pundle : pundles) {
            paths.add(pundle.getDescriptor());
        }
        return paths;
    }

    @Override
    public Collection<? extends ChangeLogSet.AffectedFile> getAffectedFiles() {
        return pundles;
    }

    @Override
    public String getCommitId() {
        return null;
    }

    public String getCommitter() {
        return committer;
    }

    @Override
    public long getTimestamp() {
        return timestamp == null ? 0 : timestamp.getTime();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getFormattedTimestamp() {
        if (timestamp == null) return "<time unknown>";

        return DateFormat.getDateTimeInstance().format(timestamp);
    }

    public void updateTimestamp(String laterTimestampString) {
        final Date newTimestamp = parseTimestamp(laterTimestampString);
        if (timestamp == null || newTimestamp.after(timestamp)) {
            timestamp = newTimestamp;
        }
    }

    public void addPundle(ChangedPundle pundle) {
        pundles.add(pundle);
    }

    private Date parseTimestamp(String s) {
        if (s == null) return null;

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            return formatter.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}
