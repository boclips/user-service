package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.response.accessrule.ContentPackageResource
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

interface ContentPackagesClient {
    @RequestLine("GET /v1/content-packages/{id}")
    fun find(@Param("id") id: String): ContentPackageResource

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            feignClient: Client,
            tokenFactory: TokenFactory? = null
        ): ContentPackagesClient {
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
                .target(ContentPackagesClient::class.java, apiUrl)
        }
    }
}
