package io.jenkins.plugins.reverse_merge.domain

data class PullRequest(
    val branchName: String,
    val title: String,
    val owner: BitbucketUser,
)
