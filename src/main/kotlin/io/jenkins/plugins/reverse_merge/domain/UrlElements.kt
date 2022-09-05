package io.jenkins.plugins.reverse_merge.domain

data class UrlElements(
    val gitRepositoryHostingServiceUrl: String,
    val projectName: String,
    val repositoryName: String,
)
