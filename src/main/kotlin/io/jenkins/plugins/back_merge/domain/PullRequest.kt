package io.jenkins.plugins.back_merge.domain

data class PullRequest(
    val fromBranch: Branch,
    val owner: BitbucketUser,
) {
    fun buildBackMergePullRequestTitle(baseBranch: Branch) =
        "Back merge from ${baseBranch.displayId} to ${fromBranch.displayId}"

    fun buildBackMergePullRequestDescription(baseBranch: Branch) = """
        Hello! This is Back Merge plugin.
        
        Now ${baseBranch.displayId} branch has been updated, so please update your branch in the one of the following ways.
        1. Approve this PR and push merge button.
        2. Merge ${baseBranch.displayId} branch into your branch manually and push it (this PR will be closed automatically).
        
        If this PR is conflicted, please resolve it ASAP.
        
        Thank you!
    """.trimIndent()
}
