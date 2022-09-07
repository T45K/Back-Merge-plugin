package io.jenkins.plugins.back_merge.usecase

import io.jenkins.plugins.back_merge.domain.HttpClient
import io.jenkins.plugins.back_merge.domain.UrlElements

class BackMergeUsecase(private val httpClient: HttpClient) {
    fun executeBackMerge(urlElements: UrlElements, baseBranchName: String) {
        val baseBranch = httpClient.fetchBranchByName(urlElements, baseBranchName)
            ?: throw RuntimeException() // TODO: use appropriate exception
        val pullRequests = httpClient.fetchOpenPullRequests(urlElements)
        for (pullRequest in pullRequests) {
            try {
                httpClient.sendBackMergePullRequest(
                    urlElements,
                    pullRequest.buildBackMergePullRequestTitle(baseBranch),
                    baseBranch,
                    pullRequest.fromBranch,
                    pullRequest.owner,
                    pullRequest.buildBackMergePullRequestDescription(baseBranch),
                )
            } catch (e: Exception) {
                // TODO: logging?
            }
        }
    }
}
