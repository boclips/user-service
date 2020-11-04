package com.boclips.users.api.httpclient

import com.boclips.users.api.request.CreateApiUserRequest
import feign.Param
import feign.RequestLine

interface ApiUsersClient {
    @RequestLine("PUT /v1/api-users/{id}")
    fun createApiUser(@Param("id") id: String, createApiUserRequest: CreateApiUserRequest)
}
