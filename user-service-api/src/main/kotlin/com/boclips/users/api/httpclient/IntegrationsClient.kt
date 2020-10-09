package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.request.SynchroniseIntegrationUserRequest
import com.boclips.users.api.response.integration.SynchUserResource
import com.fasterxml.jackson.databind.ObjectMapper
import feign.Feign
import feign.Logger
import feign.RequestLine
import feign.RequestTemplate
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

interface IntegrationsClient {
    @RequestLine("PUT /v1/integrations/deployments")
    fun synchroniseUser (request: SynchroniseIntegrationUserRequest): SynchUserResource

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            tokenFactory: TokenFactory? = null
        ): IntegrationsClient {
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
                .target(IntegrationsClient::class.java, apiUrl)
        }
    }
}
