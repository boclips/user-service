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

    override fun validateCaptchaToken(token: String): Boolean {
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
                    logger.info(errors) { "The reCaptcha validation has failed." }
                    return false
                }

                logger.info { "User's reCaptcha score: ${response.score}. Threshold Score: ${this.properties.threshold}" }

                return response.score!! > this.properties.threshold
            }

            throw GoogleRecaptchaException("Google reCaptcha returned ${validationResponse.statusCode} but should have returned ${HttpStatus.OK}")
        } catch (ex: Exception) {
            logger.error { "Exception occurred while requesting token validation for $token: $ex" }
            throw GoogleRecaptchaException("Failed to validate token $token", ex)
        }
    }
}
