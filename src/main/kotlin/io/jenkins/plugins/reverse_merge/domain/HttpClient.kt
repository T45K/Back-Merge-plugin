package io.jenkins.plugins.reverse_merge.domain

interface HttpClient {

    fun fetchBranchByName(urlElements: UrlElements, branchName: String): Branch?
    fun fetchOpenPullRequests(urlElements: UrlElements): List<PullRequest>
    fun sendReverseMergePullRequest(
        urlElements: UrlElements,
        fromBranch: Branch,
        toBranch: Branch,
        reviewer: BitbucketUser
    )
}
