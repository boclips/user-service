package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.request.CreateApiUserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import feign.Feign
import feign.Logger
import feign.Param
import feign.RequestLine
import feign.RequestTemplate
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

interface ApiUsersClient {
    @RequestLine("PUT /v1/api-users/{id}")
    fun createApiUser(@Param("id") id: String, createApiUserRequest: CreateApiUserRequest)

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            tokenFactory: TokenFactory? = null
        ): ApiUsersClient {
            return Feign.builder()
                .client(OkHttpClient())
                .encoder(JacksonEncoder(objectMapper))
                .decoder(JacksonDecoder(objectMapper))
                .requestInterceptor { template: RequestTemplate ->
                    if (tokenFactory != null) {
                        template.header("Authorization", "Bearer ${tokenFactory.getAccessToken()}")
                    }
                }
                .logLevel(Logger.Level.BASIC)
                .logger(Slf4jLogger())
                .target(ApiUsersClient::class.java, apiUrl)
        }
    }
}
