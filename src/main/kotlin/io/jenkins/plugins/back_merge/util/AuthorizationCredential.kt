package io.jenkins.plugins.back_merge.util

import okhttp3.Credentials

sealed interface AuthorizationCredential {

    fun asAuthorizationHeader(): String

    data class Basic(private val username: String, private val password: String) : AuthorizationCredential {
        override fun asAuthorizationHeader(): String = Credentials.basic(username, password)
    }

    data class Barer(private val secret: String) : AuthorizationCredential {
        override fun asAuthorizationHeader(): String = "Barer: $secret"
    }
}

object AuthorizationCredentialFactory {
    fun create(username: String, password: String, httpAccessToken: String): AuthorizationCredential =
        when {
            username.isNotBlank() && password.isNotBlank() -> AuthorizationCredential.Basic(username, password)
            httpAccessToken.isNotBlank() -> AuthorizationCredential.Barer(httpAccessToken)
            else -> throw NoSuchElementException()
        }
}
