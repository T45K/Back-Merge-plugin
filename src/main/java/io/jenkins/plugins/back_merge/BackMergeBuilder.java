package io.jenkins.plugins.back_merge;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("unused")
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

    @NotNull
    public String getProjectName() {
        return projectName;
    }

    @NotNull
    public String getRepositoryName() {
        return repositoryName;
    }

    @NotNull
    public String getBaseBranchName() {
        return baseBranchName;
    }

    @Override
    public void perform(@NotNull Run<?, ?> run, @NotNull FilePath workspace, @NotNull EnvVars env, @NotNull Launcher launcher, @NotNull TaskListener listener) {
        final DescriptorImpl descriptor = (DescriptorImpl) super.getDescriptor();
        final UsernamePasswordCredentialsImpl usernamePasswordCredential = Optional.ofNullable(CredentialsProvider.findCredentialById(descriptor.getBasicAuthCredentialId(), UsernamePasswordCredentialsImpl.class, run))
            .orElseThrow(); // TODO: appropriate exception
        new EntryPoint(
            listener.getLogger(),
            usernamePasswordCredential.getUsername(),
            usernamePasswordCredential.getPassword().getPlainText(),
            descriptor.getGitRepositoryHostingServiceUrl(),
            projectName, repositoryName, baseBranchName
        ).main();
    }

    @Symbol("backMerge")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private String gitRepositoryHostingServiceUrl;
        private String basicAuthCredentialId;

        public DescriptorImpl() {
            super.load();
        }

        public String getGitRepositoryHostingServiceUrl() {
            return gitRepositoryHostingServiceUrl;
        }

        public String getBasicAuthCredentialId() {
            return basicAuthCredentialId;
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) {
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

        public ListBoxModel doFillBasicAuthCredentialIdItems() {
            return new StandardListBoxModel()
                .includeEmptyValue()
                .includeMatchingAs(ACL.SYSTEM, Jenkins.get(), StandardCredentials.class, Collections.emptyList(),
                    type -> type instanceof UsernamePasswordCredentialsImpl || type instanceof StringCredentialsImpl);
        }
    }
}
