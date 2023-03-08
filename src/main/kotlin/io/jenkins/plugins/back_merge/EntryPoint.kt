package io.jenkins.plugins.back_merge

import io.jenkins.plugins.back_merge.domain.UrlElements
import io.jenkins.plugins.back_merge.infrastructure.HttpClientImpl
import io.jenkins.plugins.back_merge.usecase.BackMergeUsecase
import io.jenkins.plugins.back_merge.util.AuthorizationCredentialFactory
import java.io.PrintStream

class EntryPoint(
    private val logger: PrintStream,
    private val basicAuthUserName: String,
    private val basicAuthUserPassword: String,
    private val httpAccessToken: String,
    private val gitRepositoryHostingServiceUrl: String,
    private val projectName: String,
    private val repositoryName: String,
    private val baseBranchName: String,
) {
    fun main() {
        JenkinsLogger.delegate = logger
        val credential =
            AuthorizationCredentialFactory.create(basicAuthUserName, basicAuthUserPassword, httpAccessToken)
        val httpClient = HttpClientImpl(credential)
        val usecase = BackMergeUsecase(httpClient)
        val urlElements = UrlElements(gitRepositoryHostingServiceUrl, projectName, repositoryName)

        usecase.executeBackMerge(urlElements, baseBranchName)
    }
}
