package io.jenkins.plugins.back_merge.domain

interface HttpClient {

    fun fetchBranchByName(urlElements: UrlElements, branchName: String): Branch?
    fun fetchOpenPullRequests(urlElements: UrlElements): List<PullRequest>
    fun sendBackMergePullRequest(
        urlElements: UrlElements,
        title: String,
        fromBranch: Branch,
        toBranch: Branch,
        reviewer: BitbucketUser,
        description: String,
    )
}
