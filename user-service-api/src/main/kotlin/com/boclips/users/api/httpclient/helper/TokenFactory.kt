package com.boclips.users.api.httpclient.helper

interface TokenFactory {
    fun getAccessToken(): String
}
