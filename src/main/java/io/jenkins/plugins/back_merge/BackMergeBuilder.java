package io.jenkins.plugins.back_merge;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Optional;

public class BackMergeBuilder extends Builder implements SimpleBuildStep {
    @NotNull
    private final String projectName;
    @NotNull
    private final String repositoryName;
    @NotNull
    private final String baseBranchName;

    @DataBoundConstructor
    public BackMergeBuilder(@NotNull final String projectName, @NotNull final String repositoryName, @NotNull final String baseBranchName) {
        this.projectName = projectName;
        this.repositoryName = repositoryName;
        this.baseBranchName = baseBranchName;
    }

    @Override
    public void perform(@NotNull Run<?, ?> run, @NotNull FilePath workspace, @NotNull EnvVars env, @NotNull Launcher launcher, @NotNull TaskListener listener) {
        final DescriptorImpl descriptor = (DescriptorImpl) super.getDescriptor();
        final UsernamePasswordCredentialsImpl usernamePasswordCredential = Optional.ofNullable(CredentialsProvider.findCredentialById(descriptor.getBasicAuthCredentialId(), UsernamePasswordCredentialsImpl.class, run))
            .orElseThrow(); // TODO: appropriate exception
        new EntryPoint(
            usernamePasswordCredential.getUsername(),
            usernamePasswordCredential.getPassword().getPlainText(),
            descriptor.getGitRepositoryHostingServiceUrl(),
            projectName, repositoryName, baseBranchName).main();
    }

    @Symbol("backMerge")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private String gitRepositoryHostingServiceUrl;
        private String basicAuthCredentialId;

        public DescriptorImpl() {
            super.load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            final JSONObject globalSettings = json.getJSONObject("backMerge");
            this.gitRepositoryHostingServiceUrl = globalSettings.getString("gitRepositoryHostingServiceUrl");
            this.basicAuthCredentialId = globalSettings.getOrDefault("basicAuthCredentialId", "").toString();
            super.save();
            return true;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        @NotNull
        public String getDisplayName() {
            return Messages.BackMergeBuilder_DescriptorImpl_DisplayName();
        }

        public String getGitRepositoryHostingServiceUrl() {
            return gitRepositoryHostingServiceUrl;
        }

        public String getBasicAuthCredentialId() {
            return basicAuthCredentialId;
        }
    }
}
