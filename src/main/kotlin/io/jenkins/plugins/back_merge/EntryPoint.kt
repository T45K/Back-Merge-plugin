package io.jenkins.plugins.back_merge

import io.jenkins.plugins.back_merge.domain.UrlElements
import io.jenkins.plugins.back_merge.infrastructure.HttpClientImpl
import io.jenkins.plugins.back_merge.usecase.BackMergeUsecase
import java.io.PrintStream

class EntryPoint(
    private val logger: PrintStream,
    private val basicAuthUserName: String,
    private val basicAuthUserPassword: String,
    private val gitRepositoryHostingServiceUrl: String,
    private val projectName: String,
    private val repositoryName: String,
    private val baseBranchName: String,
) {
    fun main() {
        JenkinsLogger.delegate = logger
        val httpClient = HttpClientImpl(basicAuthUserName, basicAuthUserPassword)
        val usecase = BackMergeUsecase(httpClient)
        val urlElements = UrlElements(gitRepositoryHostingServiceUrl, projectName, repositoryName)

        usecase.executeBackMerge(urlElements, baseBranchName)
    }
}
