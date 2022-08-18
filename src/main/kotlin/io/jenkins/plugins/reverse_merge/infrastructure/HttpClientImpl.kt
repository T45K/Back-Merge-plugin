package io.jenkins.plugins.reverse_merge.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.reverse_merge.domain.BitbucketUser
import io.jenkins.plugins.reverse_merge.domain.HttpClient
import io.jenkins.plugins.reverse_merge.domain.PullRequest
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpClientImpl(
    private val basicAuthUserName: String,
    private val basicAuthPassword: String,
) : HttpClient {
    private val client = OkHttpClient()
    private val jsonParser = jacksonObjectMapper()

    // https://developer.atlassian.com/server/bitbucket/rest/v803/api-group-projects/#api-projects-projectkey-repos-repositoryslug-pull-requests-get
    // https://{baseurl}/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests
    override fun fetchOpenPullRequests(
        gitRepositoryHostingServiceUrl: String,
        projectName: String,
        repositoryName: String
    ): List<PullRequest> {
        val url = gitRepositoryHostingServiceUrl.toHttpUrl().newBuilder()
            .addPathSegment("rest")
            .addPathSegment("api")
            .addPathSegment("1.0")
            .addPathSegment("projects")
            .addPathSegment(projectName)
            .addPathSegment("repos")
            .addPathSegment(repositoryName)
            .addPathSegment("pull-requests")
            .build()

        val request = Request.Builder()
            .addHeader("Authorization", Credentials.basic(basicAuthUserName, basicAuthPassword))
            .url(url)
            .get()
            .build()

        return client.newCall(request)
            .execute()
            .use { it.body.string() }
            .let(jsonParser::readTree)
            .flatMap { it["values"] }
            .map { json ->
                PullRequest(
                    json["fromRef"]["displayId"].asText(),
                    json["title"].asText(),
                    BitbucketUser(json["participants"].first { it["role"].asText() == "AUTHOR" }["user"]["id"].asInt())
                )
            }
    }

    override fun sendReverseMergePullRequest() {
        TODO("Not yet implemented")
    }
}
