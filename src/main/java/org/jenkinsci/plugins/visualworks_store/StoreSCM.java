package org.jenkinsci.plugins.visualworks_store;


import hudson.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.scm.*;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class StoreSCM extends SCM {
    private final String repositoryName;
    private List<PundleSpec> pundles;
    private final String versionRegex;
    private final String minimumBlessingLevel;
    private final boolean generateParcelBuilderInputFile;
    private final String parcelBuilderInputFilename;

    @DataBoundConstructor
    public StoreSCM(String repositoryName, List<PundleSpec> pundles, String versionRegex, String minimumBlessingLevel,
                    boolean generateParcelBuilderInputFile, String parcelBuilderInputFilename) {
        if (pundles == null) {
            pundles = new ArrayList<PundleSpec>();
        }
        this.repositoryName = repositoryName;
        this.pundles = pundles;
        this.versionRegex = versionRegex;
        this.minimumBlessingLevel = minimumBlessingLevel;
        this.generateParcelBuilderInputFile = generateParcelBuilderInputFile;
        this.parcelBuilderInputFilename = parcelBuilderInputFilename;
    }

    @Override
    public boolean requiresWorkspaceForPolling() {
        return false;
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> project, Launcher launcher,
                                                      FilePath workspace, TaskListener taskListener,
                                                      SCMRevisionState _baseline)
            throws IOException, InterruptedException {
        final AbstractBuild<?, ?> lastBuild = project.getLastBuild();
        if (lastBuild == null) {
            taskListener.getLogger().println("No existing build. Scheduling a new one.");
            return PollingResult.BUILD_NOW;
        }

        StoreRevisionState baseline = (StoreRevisionState) _baseline;

        // If the Multiple SCMs plugin is being used to check different Store repositories,
        // we may be passed a baseline for a different repository.  Handle that by going to
        // look for the correct baseline in the build.
        //
        // In the normal case (only one repository), we will have the correct baseline and
        // don't need to do the extra work.
        if (!isRelevantRevisionState(baseline)) {
            baseline = findCorrectBaseline(lastBuild);
            if (baseline == null) {
                taskListener.getLogger().println("New repository. Scheduling a new build.");
                return PollingResult.BUILD_NOW;
            }
        }

        ArgumentListBuilder builder = preparePollingCommand(getDescriptor().getScript());

        String output;
        try {
            output = new StoreCommandRunner().runCommand(builder, launcher, workspace, taskListener);
        } catch (StoreCommandFailure error) {
            return PollingResult.NO_CHANGES;
        }

        final StoreRevisionState current = StoreRevisionState.parse(repositoryName, output);
        boolean changes = current.hasChangedFrom(baseline, taskListener);

        return new PollingResult(baseline, current,
                changes ? PollingResult.Change.SIGNIFICANT : PollingResult.Change.NONE);
    }

    @Override
    public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher, FilePath workspace,
                            BuildListener buildListener, File changeLogFile) throws IOException, InterruptedException {
        AbstractBuild<?, ?> lastBuild = build.getPreviousBuild();
        Calendar lastBuildTime = lastBuild == null ? midnight() : lastBuild.getTimestamp();
        ArgumentListBuilder builder = prepareCheckoutCommand(getDescriptor().getScript(), lastBuildTime,
                build.getTimestamp(), changeLogFile);

        String output;
        try {
            output = new StoreCommandRunner().runCommand(builder, launcher, workspace, buildListener);
        } catch (StoreCommandFailure storeCommandFailure) {
            throw new AbortException("Error launching command");
        }

        if (!changeLogFile.exists()) {
            createEmptyChangeLog(changeLogFile, buildListener, "log");
        }

        StoreRevisionState currentState = StoreRevisionState.parse(repositoryName, output);
        build.addAction(currentState);

        return true;
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return new StoreChangeLogParser();
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> abstractBuild, Launcher launcher,
                                                   TaskListener taskListener)
            throws IOException, InterruptedException {
        // The revision state is added to the build as part of checkout(), so this will not be called.
        return null;
    }

    private StoreRevisionState findCorrectBaseline(AbstractBuild<?, ?> lastBuild) {
        for (AbstractBuild<?, ?> build = lastBuild; build != null; build = lastBuild.getPreviousBuild()) {
            List<StoreRevisionState> revisionStates = build.getActions(StoreRevisionState.class);
            for (StoreRevisionState state : revisionStates) {
                if (isRelevantRevisionState(state)) {
                    return state;
                }
            }

            if (!revisionStates.isEmpty()) return null;
        }
        return null;
    }

    private boolean isRelevantRevisionState(StoreRevisionState state) {
        return state != null && state.getRepositoryName().equals(repositoryName);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public List<PundleSpec> getPundles() {
        return pundles;
    }

    public String getVersionRegex() {
        return versionRegex;
    }

    public String getMinimumBlessingLevel() {
        return minimumBlessingLevel;
    }

    public boolean isGenerateParcelBuilderInputFile() {
        return generateParcelBuilderInputFile;
    }

    public String getParcelBuilderInputFilename() {
        return parcelBuilderInputFilename;
    }

    ArgumentListBuilder preparePollingCommand(String storeScript) {
        ArgumentListBuilder builder = new ArgumentListBuilder();

        builder.add(storeScript);
        builder.add("-repository", repositoryName);
        builder.add("-packages");
        for (PundleSpec spec : pundles) {
            builder.add(spec.getName());
        }
        builder.add("-versionRegex", versionRegex);
        builder.add("-blessedAtLeast", minimumBlessingLevel);

        return builder;
    }

    ArgumentListBuilder prepareCheckoutCommand(String storeScript,
                                               Calendar lastBuildTime, Calendar currentBuildTime,
                                               File changeLogFile) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        ArgumentListBuilder builder = new ArgumentListBuilder();
        builder.add(storeScript);
        builder.add("-repository", repositoryName);
        builder.add("-packages");
        for (PundleSpec spec : pundles) {
            builder.add(spec.getName());
        }
        builder.add("-versionRegex", versionRegex);
        builder.add("-blessedAtLeast", minimumBlessingLevel);
        builder.add("-since", formatter.format(lastBuildTime.getTime()));
        builder.add("-now", formatter.format(currentBuildTime.getTime()));
        builder.add("-changelog", changeLogFile.getPath());

        if (generateParcelBuilderInputFile) {
            builder.add("-parcelBuilderFile", parcelBuilderInputFilename);
        }

        return builder;
    }

    private Calendar midnight() {
        final Calendar midnight = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        return midnight;
    }

    @Extension
    public static final class DescriptorImpl extends SCMDescriptor<StoreSCM> {
        @CopyOnWrite
        private volatile StoreScript[] storeScripts = new StoreScript[0];

        public DescriptorImpl() {
            super(StoreSCM.class, null);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Store";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            final List<StoreScript> scriptList = req.bindParametersToList(StoreScript.class, "script.");
            storeScripts = scriptList.toArray(new StoreScript[scriptList.size()]);
            save();
            return true;
        }

        @SuppressWarnings("UnusedDeclaration")
        public ListBoxModel doFillMinimumBlessingLevelItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Broken");
            items.add("Work In Progress");
            items.add("Development");
            items.add("To Review");
            items.add("Patch");
            items.add("Integration-Ready");
            items.add("Integrated");
            items.add("Ready to Merge");
            items.add("Merged");
            items.add("Tested");
            items.add("Internal Release");
            items.add("Released");
            return items;
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getDefaultVersionRegex() {
            return ".+";
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getDefaultMinimumBlessingLevel() {
            return "Development";
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getDefaultParcelBuilderInputFilename() {
            return "parcelsToBuild";
        }

        // Temporary backwards compatibility
        public String getScript() {
            return storeScripts[0].getPath();
        }

        public StoreScript[] getStoreScripts() {
            return storeScripts;
        }

        public void setStoreScripts(StoreScript... scripts) {
            storeScripts = scripts;
            save();
        }
    }
}
