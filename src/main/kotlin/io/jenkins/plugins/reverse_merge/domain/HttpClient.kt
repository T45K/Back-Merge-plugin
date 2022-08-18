package io.jenkins.plugins.reverse_merge.domain

interface HttpClient {
    fun fetchOpenPullRequests(
        gitRepositoryHostingServiceUrl: String,
        projectName: String,
        repositoryName: String,
    ): List<PullRequest>

    fun sendReverseMergePullRequest()
}
