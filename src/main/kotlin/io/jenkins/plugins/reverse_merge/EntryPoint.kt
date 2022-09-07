package io.jenkins.plugins.reverse_merge

import io.jenkins.plugins.reverse_merge.domain.UrlElements
import io.jenkins.plugins.reverse_merge.infrastructure.HttpClientImpl
import io.jenkins.plugins.reverse_merge.usecase.ReverseMergeUsecase

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
        val usecase = ReverseMergeUsecase(httpClient)
        val urlElements = UrlElements(gitRepositoryHostingServiceUrl, projectName, repositoryName)

        usecase.executeReverseMerge(urlElements, baseBranchName)
    }
}
