package org.jenkinsci.plugins.visualworks_store;


import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.scm.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;

public class StoreSCM extends SCM {
    private final String repositoryName;

    @DataBoundConstructor
    public StoreSCM(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> abstractBuild, Launcher launcher, TaskListener taskListener) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> abstractProject, Launcher launcher, FilePath filePath, TaskListener taskListener, SCMRevisionState scmRevisionState) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkout(AbstractBuild<?, ?> abstractBuild, Launcher launcher, FilePath filePath, BuildListener buildListener, File file) throws IOException, InterruptedException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends SCMDescriptor<StoreSCM> {
        private String script;

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
            req.bindJSON(this, json);
            save();
            return true;
        }

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }
    }
}
