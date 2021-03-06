package com.boclips.users.api.httpclient.helper

import java.time.Duration
import java.time.Instant

class ServiceAccountTokenFactory(serviceAccountCredentials: ServiceAccountCredentials) :
    TokenFactory {
    private val client: KeycloakClient = KeycloakClient.create(serviceAccountCredentials)
    private var lastRefreshTime: Instant? = null
    private lateinit var currentToken: TokenResponse

    override fun getAccessToken(): String {
        if (shouldRefreshToken()) {
            refreshToken()
        }

        return currentToken.access_token!!
    }

    @Synchronized
    private fun shouldRefreshToken(): Boolean {
        if (lastRefreshTime == null) {
            return true
        }

        return hasTokenExpired(lastRefreshTime!!)
    }

    private fun hasTokenExpired(previousRefreshTime: Instant): Boolean {
        val timeElapsed: Long = Duration.between(previousRefreshTime, Instant.now()).seconds

        // Add some seconds to guard against using an out of date token
        return (timeElapsed + 15) >= currentToken.expires_in!!
    }

    private fun refreshToken() {
        currentToken = client.getToken(TokenRequest())
        lastRefreshTime = Instant.now()
    }
}
