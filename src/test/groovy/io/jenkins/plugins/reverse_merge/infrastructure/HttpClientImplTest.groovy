package io.jenkins.plugins.reverse_merge.infrastructure

import io.jenkins.plugins.reverse_merge.domain.BitbucketUser
import io.jenkins.plugins.reverse_merge.domain.PullRequest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import spock.lang.Specification

class HttpClientImplTest extends Specification {
    private final def mockWebServer = new MockWebServer()

    def 'fetchOpenPullRequests can deserialize JSON as API response'() {
        given:
        mockWebServer.enqueue(new MockResponse().setBody(json))

        final def sut = new HttpClientImpl('hoge', 'fuga')

        expect:
        sut.fetchOpenPullRequests(mockWebServer.url('').toString(), 'foo', 'bar') ==
            [new PullRequest('feature-ABC-1233', 'Talking Nerdy', new BitbucketUser(101))]

        cleanup:
        mockWebServer.shutdown()
    }

    private final def json = '''
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
