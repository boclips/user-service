package com.boclips.users.api.httpclient

import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TokenFactory
import com.boclips.users.api.request.OrganisationFilterRequest
import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.fasterxml.jackson.databind.ObjectMapper
import feign.Client
import feign.Feign
import feign.Logger
import feign.Param
import feign.QueryMap
import feign.RequestLine
import feign.RequestTemplate
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger

interface OrganisationsClient {
    @RequestLine("GET /v1/organisations/{id}")
    fun getOrganisation(@Param("id") id: String): OrganisationResource

    @RequestLine("GET /v1/organisations")
    fun getOrganisations(@QueryMap filterRequest: OrganisationFilterRequest = OrganisationFilterRequest()): OrganisationsResource

    companion object {
        @JvmStatic
        fun create(
            apiUrl: String,
            objectMapper: ObjectMapper = ObjectMapperDefinition.default(),
            feignClient: Client,
            tokenFactory: TokenFactory? = null
        ): OrganisationsClient {
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
                .target(OrganisationsClient::class.java, apiUrl)
        }
    }
}
