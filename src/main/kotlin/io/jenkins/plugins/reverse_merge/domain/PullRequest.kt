package io.jenkins.plugins.reverse_merge.domain

data class PullRequest(
    val fromBranch: Branch,
    val owner: BitbucketUser,
) {
    fun buildReverseMergePullRequestTitle(baseBranch: Branch) =
        "Reverse merge from ${baseBranch.displayId} to ${fromBranch.displayId}"

    fun buildReverseMergePullRequestDescription(baseBranch: Branch) = """
        Hello! This is Reverse Merge plugin.
        
        ${baseBranch.displayId} branch was updated, so please your branch by either ways.
        1. Approve this PR and push merge button.
        2. Merge ${baseBranch.displayId} branch into your branch manually and push it (this PR will be closed automatically).
        
        If this PR is conflicted, please resolve it ASAP.
        
        Thank you!
    """.trimIndent()
}
