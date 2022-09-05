package io.jenkins.plugins.reverse_merge.usecase

import io.jenkins.plugins.reverse_merge.domain.HttpClient
import io.jenkins.plugins.reverse_merge.domain.UrlElements

class ReverseMergeUsecase(private val httpClient: HttpClient) {
    fun executeReverseMerge(urlElements: UrlElements, baseBranchName: String) {
        val baseBranch = httpClient.fetchBranchByName(urlElements, baseBranchName)
            ?: throw RuntimeException() // TODO: use appropriate exception
        val pullRequests = httpClient.fetchOpenPullRequests(urlElements)
        for (pullRequest in pullRequests) {
            try {
                httpClient.sendReverseMergePullRequest(
                    urlElements,
                    pullRequest.buildReverseMergePullRequestTitle(baseBranch),
                    baseBranch,
                    pullRequest.fromBranch,
                    pullRequest.owner,
                    pullRequest.buildReverseMergePullRequestDescription(baseBranch),
                )
            } catch (e: Exception) {
                // TODO: logging?
            }
        }
    }
}
