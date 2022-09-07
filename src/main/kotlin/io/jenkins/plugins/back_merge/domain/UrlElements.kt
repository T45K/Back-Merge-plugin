package io.jenkins.plugins.back_merge.domain

data class UrlElements(
    val gitRepositoryHostingServiceUrl: String,
    val projectName: String,
    val repositoryName: String,
)
