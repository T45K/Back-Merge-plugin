package io.jenkins.plugins.back_merge.infrastructure

import groovy.json.JsonSlurper
import io.jenkins.plugins.back_merge.domain.BitbucketUser
import io.jenkins.plugins.back_merge.domain.Branch
import io.jenkins.plugins.back_merge.domain.PullRequest
import io.jenkins.plugins.back_merge.domain.UrlElements
import io.jenkins.plugins.back_merge.util.AuthorizationCredential
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import spock.lang.Specification

class HttpClientImplTest extends Specification {
    private final def mockWebServer = new MockWebServer()
    private final def sut = new HttpClientImpl(new AuthorizationCredential.Basic('hoge', 'fuga'))

    def 'fetchBranchByName returns branch object from JSON'() {
        given:
        mockWebServer.enqueue(new MockResponse().setBody(branchJson))

        final def urlElements = new UrlElements(mockWebServer.url('').toString(), 'foo', 'bar')

        when:
        def actualBranch = sut.fetchBranchByName(urlElements, name)

        then:
        actualBranch == expectedBranch
        mockWebServer.takeRequest().requestUrl == mockWebServer.url('rest/api/1.0/projects/foo/repos/bar/branches')

        where:
        name     | expectedBranch
        'master' | new Branch('refs/heads/master', 'master')
        'work'   | null
    }

    def 'fetchOpenPullRequests can deserialize JSON as API response'() {
        given:
        mockWebServer.enqueue(new MockResponse().setBody(prJson))

        final def urlElements = new UrlElements(mockWebServer.url('').toString(), 'foo', 'bar')

        when:
        def pullRequests = sut.fetchOpenPullRequests(urlElements)

        then:
        pullRequests == [new PullRequest(new Branch('refs/heads/feature-ABC-123', 'feature-ABC-1233'), new BitbucketUser('jcitizen'))]
        mockWebServer.takeRequest().requestUrl == mockWebServer.url('rest/api/1.0/projects/foo/repos/bar/pull-requests')
    }

    def 'sendBackMergePullRequest sends toRef id, fromRef id, and reviewer id'() {
        given:
        mockWebServer.enqueue(new MockResponse().setResponseCode(200))

        final def urlElements = new UrlElements(mockWebServer.url('').toString(), 'foo', 'bar')
        final def masterBranch = new Branch('refs/heads/master', 'master')
        final def workBranch = new Branch('refs/heads/work', 'work')
        final def user = new BitbucketUser('jcitizen')

        when:
        sut.sendBackMergePullRequest(urlElements, 'title', masterBranch, workBranch, user, 'description')

        then:
        mockWebServer.requestCount == 1
        final def request = mockWebServer.takeRequest()
        new JsonSlurper().parseText(request.body.readUtf8()) == [
            title      : 'title',
            fromRef    : [id: 'refs/heads/master'],
            toRef      : [id: 'refs/heads/work'],
            reviewers  : [[user: [name: 'jcitizen']]],
            description: 'description'
        ]
        request.requestUrl == mockWebServer.url('rest/api/1.0/projects/foo/repos/bar/pull-requests')
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    /*
    heredoc
     */
    private final def branchJson = '''
{
  "values": [
    {
      "default": true,
      "displayId": "master",
      "latestCommit": "8d51122def5632836d1cb1026e879069e10a1e13",
      "latestChangeset": "8d51122def5632836d1cb1026e879069e10a1e13",
      "id": "refs/heads/master"
    }
  ],
  "size": 1,
  "limit": 25,
  "isLastPage": true,
  "nextPageStart": 2154,
  "start": 2154
}
'''

    private final def prJson = '''
{
  "values": [
    {
      "locked": true,
      "version": 2154,
      "id": 1,
      "state": "OPEN",
      "open": true,
      "updatedDate": 14490759200,
      "closedDate": 19990759200,
      "fromRef": {
        "id": "refs/heads/feature-ABC-123",
        "type": "BRANCH",
        "displayId": "feature-ABC-1233",
        "latestCommit": "babecafebabecafebabecafebabecafebabecafe",
        "repository": {
          "name": "My repo",
          "id": 2154,
          "state": "AVAILABLE",
          "public": true,
          "hierarchyId": "e3c939f9ef4a7fae272e",
          "statusMessage": "Available",
          "archived": true,
          "forkable": true,
          "relatedLinks": {},
          "partition": 2154,
          "defaultBranch": "main",
          "origin": {
            "name": "My repo",
            "id": 2154,
            "state": "AVAILABLE",
            "public": true,
            "hierarchyId": "e3c939f9ef4a7fae272e",
            "statusMessage": "Available",
            "archived": true,
            "forkable": true,
            "relatedLinks": {},
            "partition": 2154,
            "defaultBranch": "main",
            "project": {
              "name": "My Cool Project",
              "key": "PRJ",
              "id": 2154,
              "type": "NORMAL",
              "public": true,
              "avatar": "<string>",
              "description": "The description for my cool project",
              "namespace": "<string>",
              "scope": "PROJECT"
            },
            "description": "My repo description",
            "scope": "REPOSITORY",
            "scmId": "git",
            "slug": "my-repo"
          },
          "project": {
            "name": "My Cool Project",
            "key": "PRJ",
            "id": 2154,
            "type": "NORMAL",
            "public": true,
            "avatar": "<string>",
            "description": "The description for my cool project",
            "namespace": "<string>",
            "scope": "PROJECT"
          },
          "description": "My repo description",
          "scope": "REPOSITORY",
          "scmId": "git",
          "slug": "my-repo"
        }
      },
      "participants": [
        {
          "lastReviewedCommit": "7549846524f8aed2bd1c0249993ae1bf9d3c9998",
          "approved": true,
          "status": "UNAPPROVED",
          "user": {
            "name": "jcitizen",
            "id": 101,
            "type": "NORMAL",
            "displayName": "Jane Citizen",
            "emailAddress": "jane@example.com",
            "active": true,
            "slug": "jcitizen"
          },
          "role": "AUTHOR"
        }
      ],
      "reviewers": [
        {
          "lastReviewedCommit": "7549846524f8aed2bd1c0249993ae1bf9d3c9998",
          "approved": true,
          "status": "UNAPPROVED",
          "user": {
            "name": "jcitizen",
            "id": 101,
            "type": "NORMAL",
            "displayName": "Jane Citizen",
            "emailAddress": "jane@example.com",
            "active": true,
            "slug": "jcitizen"
          },
          "role": "AUTHOR"
        }
      ],
      "createdDate": 13590759200,
      "description": "It is a kludge, but put the tuple from the database in the cache.",
      "toRef": {
        "id": "refs/heads/feature-ABC-123",
        "type": "BRANCH",
        "displayId": "feature-ABC-1233",
        "latestCommit": "babecafebabecafebabecafebabecafebabecafe",
        "repository": {
          "name": "My repo",
          "id": 2154,
          "state": "AVAILABLE",
          "public": true,
          "hierarchyId": "e3c939f9ef4a7fae272e",
          "statusMessage": "Available",
          "archived": true,
          "forkable": true,
          "relatedLinks": {},
          "partition": 2154,
          "defaultBranch": "main",
          "origin": {
            "name": "My repo",
            "id": 2154,
            "state": "AVAILABLE",
            "public": true,
            "hierarchyId": "e3c939f9ef4a7fae272e",
            "statusMessage": "Available",
            "archived": true,
            "forkable": true,
            "relatedLinks": {},
            "partition": 2154,
            "defaultBranch": "main",
            "project": {
              "name": "My Cool Project",
              "key": "PRJ",
              "id": 2154,
              "type": "NORMAL",
              "public": true,
              "avatar": "<string>",
              "description": "The description for my cool project",
              "namespace": "<string>",
              "scope": "PROJECT"
            },
            "description": "My repo description",
            "scope": "REPOSITORY",
            "scmId": "git",
            "slug": "my-repo"
          },
          "project": {
            "name": "My Cool Project",
            "key": "PRJ",
            "id": 2154,
            "type": "NORMAL",
            "public": true,
            "avatar": "<string>",
            "description": "The description for my cool project",
            "namespace": "<string>",
            "scope": "PROJECT"
          },
          "description": "My repo description",
          "scope": "REPOSITORY",
          "scmId": "git",
          "slug": "my-repo"
        }
      },
      "author": {
        "user": {
          "name": "jcitizen",
          "id": 101,
          "type": "NORMAL",
          "displayName": "Jane Citizen",
          "emailAddress": "jane@example.com",
          "active": true,
          "slug": "jcitizen"
        }
      },
      "title": "Talking Nerdy",
      "closed": false
    }
  ],
  "size": 1,
  "limit": 25,
  "isLastPage": true,
  "nextPageStart": 2154,
  "start": 2154
}
'''
}
