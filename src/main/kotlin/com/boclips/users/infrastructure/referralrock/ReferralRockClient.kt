package com.boclips.users.infrastructure.referralrock

import com.boclips.users.infrastructure.getContentTypeHeader
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.Base64

class ReferralRockClient(
    val properties: ReferralRockProperties,
    private val objectMapper: ObjectMapper
) {
    companion object : KLogging()

    private val restTemplate = RestTemplate()

    fun createReferral(newReferral: NewReferral): ReferralId {
        val headers = getStandardHeaders()

        val request = HttpEntity(
            objectMapper.writeValueAsString(newReferral),
            headers
        )

        try {
            val createdReferral = restTemplate.postForEntity(
                createEndpoint("/api/referrals"),
                request,
                ReferralActionResponse::class.java
            )

            if (createdReferral.statusCode == HttpStatus.CREATED) {
                logger.info { "Created referral for user ${newReferral.externalIdentifier} with referral code ${newReferral.referralCode}" }
                return ReferralId(value = createdReferral.body?.referral!!.id)
            }

            throw ReferralRockException("ReferralRock returned ${createdReferral.statusCode} but should have returned ${HttpStatus.CREATED}")
        } catch (ex: Exception) {
            logger.error { "Failed to create referral $newReferral" }
            throw ReferralRockException("Failed to create referral for ${newReferral.externalIdentifier} with referral code ${newReferral.referralCode}")
        }
    }

    private fun createEndpoint(path: String): String {
        return properties.host + path
    }

    private fun getStandardHeaders(): HttpHeaders {
        val headers = getContentTypeHeader()
        headers.add("Authorization", "${basicAuthHeaderValue()}")
        return headers
    }

    private fun basicAuthHeaderValue(): String {
        val userNamePasswordEncoded =
            String(Base64.getEncoder().encode("${properties.publicKey}:${properties.privateKey}".toByteArray()))
        return "Basic $userNamePasswordEncoded"
    }
}
