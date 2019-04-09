package com.boclips.users.infrastructure.recaptcha

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.infrastructure.getContentTypeHeader
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class GoogleRecaptchaClient(
    private val properties: GoogleRecaptchaProperties
) : CaptchaProvider {
    companion object : KLogging()

    private val restTemplate = RestTemplate()

    override fun validateCaptchaToken(token: String, identifier: String): Boolean {
        val request = HttpEntity<Void>(getContentTypeHeader())

        try {
            val validationResponse = restTemplate.postForEntity(
                this.properties.host + "/recaptcha/api/siteverify?secret={secret}&response={response}",
                request,
                RecaptchaValidationResponse::class.java,
                properties.secretKey,
                token
            )

            if (validationResponse.statusCode == HttpStatus.OK) {
                val response = validationResponse.body!!

                if (!response.success) {
                    val errors = response.errorCodes!!.joinToString(",")
                    logger.error { "The reCaptcha validation has failed. [$errors]" }
                    return false
                }

                return if (response.score!! > this.properties.threshold) {
                    logger.info { "Google reCaptcha score was above the threshold ${response.score} > ${this.properties.threshold}" }
                    true
                } else {
                    logger.warn { "Google reCaptcha score was below the threshold ${response.score} < ${this.properties.threshold}" }
                    false
                }
            }

            throw GoogleRecaptchaException("Google reCaptcha returned ${validationResponse.statusCode} but should have returned ${HttpStatus.OK}")
        } catch (ex: Exception) {
            logger.error { "Exception occurred while requesting token validation for $token: $ex" }
            throw GoogleRecaptchaException("Failed to validate token $token", ex)
        }
    }
}