package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.api.response.user.UserResource
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

interface UsersClient {
    @RequestLine("GET /v1/users/{id}")
    fun getUser(@Param("id") id: String): UserResource

    @RequestLine("GET /v1/users/{id}/access-rules")
    fun getAccessRulesOfUser(@Param("id") id: String): AccessRulesResource

    @RequestLine("GET /v1/users/{id}/shareCode/{shareCode}")
    fun getShareCode(@Param("id") id: String, @Param("shareCode") shareCode: String)

    @RequestLine("GET /v1/users/{id}/features")
    fun getUserFeatures(@Param("id") id: String): FeaturesResource

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            tokenFactory: TokenFactory? = null
        ): UsersClient {
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
                .target(UsersClient::class.java, apiUrl)
        }
    }
}
