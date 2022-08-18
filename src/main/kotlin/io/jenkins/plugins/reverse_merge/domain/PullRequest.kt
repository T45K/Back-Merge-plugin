package io.jenkins.plugins.reverse_merge.domain

data class PullRequest(
    private val branchName: String,
    private val title: String,
    private val owner: BitbucketUser,
) {
    fun isCreatedByReverseMergePlugin() {

    }
}
