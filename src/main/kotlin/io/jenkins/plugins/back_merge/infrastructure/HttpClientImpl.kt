package io.jenkins.plugins.back_merge.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.back_merge.domain.*
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpClientImpl(
    private val basicAuthUserName: String,
    private val basicAuthPassword: String,
) : HttpClient {
    private val client = OkHttpClient()
    private val jsonMapper = jacksonObjectMapper()

    // https://developer.atlassian.com/server/bitbucket/rest/v803/api-group-projects/#api-projects-projectkey-repos-repositoryslug-branches-get
    // https://{baseUrl}/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/branches
    override fun fetchBranchByName(urlElements: UrlElements, branchName: String): Branch? {
        val url = urlElements.toBranchUrl()

        val request = Request.Builder()
            .addHeader("Authorization", Credentials.basic(basicAuthUserName, basicAuthPassword))
            .url(url)
            .get()
            .build()

        return client.newCall(request)
            .execute()
            .use { it.body.string() }
            .let(jsonMapper::readTree)
            .let { it["values"] }
            .map { json ->
                Branch(
                    json["id"].asText(),
                    json["displayId"].asText()
                )
            }
            .find { it.displayId == branchName }
    }

    // https://developer.atlassian.com/server/bitbucket/rest/v803/api-group-projects/#api-projects-projectkey-repos-repositoryslug-pull-requests-get
    // https://{baseurl}/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests
    override fun fetchOpenPullRequests(urlElements: UrlElements): List<PullRequest> {
        val url = urlElements.toPullRequestUrl()

        val request = Request.Builder()
            .addHeader("Authorization", Credentials.basic(basicAuthUserName, basicAuthPassword))
            .url(url)
            .get()
            .build()

        return client.newCall(request)
            .execute()
            .use { it.body.string() }
            .let(jsonMapper::readTree)
            .let { it["values"] }
            .map { json ->
                PullRequest(
                    Branch(json["fromRef"]["id"].asText(), json["fromRef"]["displayId"].asText()),
                    BitbucketUser(json["author"]["user"]["id"].asInt())
                )
            }
    }

    // https://developer.atlassian.com/server/bitbucket/rest/v803/api-group-projects/#api-projects-projectkey-repos-repositoryslug-pull-requests-post
    // https://{baseUrl}/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests
    override fun sendBackMergePullRequest(
        urlElements: UrlElements,
        title: String,
        fromBranch: Branch,
        toBranch: Branch,
        reviewer: BitbucketUser,
        description: String,
    ) {
        val url = urlElements.toPullRequestUrl()

        val requestBody = jsonMapper.writeValueAsString(
            mapOf(
                "title" to title,
                "fromRef" to mapOf("id" to fromBranch.id),
                "toRef" to mapOf("id" to toBranch.id),
                "reviewers" to mapOf("user" to mapOf("id" to reviewer.id)),
                "description" to description
            )
        ).toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .addHeader("Authorization", Credentials.basic(basicAuthUserName, basicAuthPassword))
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute()
    }
    private fun UrlElements.toBranchUrl() = this.gitRepositoryHostingServiceUrl.toHttpUrl().newBuilder()
        .addPathSegment("rest")
        .addPathSegment("api")
        .addPathSegment("1.0")
        .addPathSegment("projects")
        .addPathSegment(this.projectName)
        .addPathSegment("repos")
        .addPathSegment(this.repositoryName)
        .addPathSegment("branches")
        .build()

    private fun UrlElements.toPullRequestUrl() = this.gitRepositoryHostingServiceUrl.toHttpUrl().newBuilder()
        .addPathSegment("rest")
        .addPathSegment("api")
        .addPathSegment("1.0")
        .addPathSegment("projects")
        .addPathSegment(this.projectName)
        .addPathSegment("repos")
        .addPathSegment(this.repositoryName)
        .addPathSegment("pull-requests")
        .build()
}
