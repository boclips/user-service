package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.user.UserResource
import com.fasterxml.jackson.databind.ObjectMapper
import feign.Client
import feign.Feign
import feign.Logger
import feign.Param
import feign.RequestLine
import feign.RequestTemplate
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger

interface UsersClient {
    @RequestLine("GET /v1/users/{id}")
    fun getUser(@Param("id") id: String): UserResource

    @RequestLine("GET /v1/users/{id}/access-rules?client={client}")
    fun getAccessRulesOfUser(@Param("id") id: String, @Param("client") client: String? = null): AccessRulesResource

    @RequestLine("GET /v1/users/{id}/shareCode/{shareCode}")
    fun getShareCode(@Param("id") id: String, @Param("shareCode") shareCode: String)

    @RequestLine("GET /v1/users/_self")
    fun getLoggedInUser(): UserResource

    @RequestLine("POST /v1/users")
    fun createApiUser(createApiUserRequest: CreateUserRequest.CreateApiUserRequest)

    @RequestLine("HEAD /v1/users/{id}")
    fun headUser(@Param("id") id: String)

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            feignClient: Client,
            tokenFactory: TokenFactory? = null
        ): UsersClient {
            return Feign.builder()
                .client(feignClient)
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
