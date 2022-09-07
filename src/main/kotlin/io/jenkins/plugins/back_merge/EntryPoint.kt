package io.jenkins.plugins.back_merge

import io.jenkins.plugins.back_merge.domain.UrlElements
import io.jenkins.plugins.back_merge.infrastructure.HttpClientImpl
import io.jenkins.plugins.back_merge.usecase.BackMergeUsecase

class EntryPoint(
    private val basicAuthUserName: String,
    private val basicAuthUserPassword: String,
    private val gitRepositoryHostingServiceUrl: String,
    private val projectName: String,
    private val repositoryName: String,
    private val baseBranchName: String,
) {
    fun main() {
        val httpClient = HttpClientImpl(basicAuthUserName, basicAuthUserPassword)
        val usecase = BackMergeUsecase(httpClient)
        val urlElements = UrlElements(gitRepositoryHostingServiceUrl, projectName, repositoryName)

        usecase.executeBackMerge(urlElements, baseBranchName)
    }
}
