package org.jenkinsci.plugins.visualworks_store;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class StoreCommandRunner {
    String runCommand(ArgumentListBuilder builder, Launcher launcher, FilePath workspace, TaskListener listener) throws StoreCommandFailure, IOException, InterruptedException {
        if (launcher == null) {
            launcher = new Launcher.LocalLauncher(listener);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        int rc = launcher.launch().pwd(workspace).cmds(builder).stdout(outputStream).stderr(errorStream).join();
        if (rc != 0) {
            listener.getLogger().println("Error running command: " + errorStream.toString());
            throw new StoreCommandFailure(errorStream.toString());
        }

        return outputStream.toString();
    }

}

class StoreCommandFailure extends Exception {
    public StoreCommandFailure(String message) {
        super(message);
    }
}
