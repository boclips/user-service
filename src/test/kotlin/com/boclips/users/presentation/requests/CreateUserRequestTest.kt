package com.boclips.users.presentation.requests

import com.boclips.users.testsupport.factories.CreateUserRequestFactory
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class CreateUserRequestTest {

    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()
    }

    @Test
    fun `validates a valid request`() {
        val validRequest = CreateUserRequestFactory.sample()
        val violations = validator.validate(validRequest)
        assertThat(violations).hasSize(0)
    }

    @Test
    fun `respects optional fields`() {
        val violations = validator.validate(
            CreateUserRequestFactory.sample(
                referralCode = null,
                analyticsId = null
            )
        )
        assertThat(violations).hasSize(0)
    }

    @Nested
    inner class Emails {
        @Test
        fun `validates email for null`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    email = null
                )
            )
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Email is required")
            assertThat(violations.map { it.message }).contains("Email must be set")
        }

        @Test
        fun `validates email for empty string`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    email = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Email must be set")
        }

        @Test
        fun `validates email for format`() {
            val longEmail = "@test.com"
            val violations =
                validator.validate(
                    CreateUserRequestFactory.sample(
                        email = longEmail
                    )
                )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Email must be valid")
        }
    }

    @Nested
    inner class Passwords {
        @Test
        fun `validates password for null`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    password = null
                )
            )
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Password is required")
            assertThat(violations.map { it.message }).contains("Password must be set")
        }

        @Test
        fun `validates password for empty string`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    password = ""
                )
            )
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Password length must be at least 8")
            assertThat(violations.map { it.message }).contains("Password must be set")
        }

        @Test
        fun `validates password for format`() {
            val shortPassword = "1234567"
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    password = shortPassword
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Password length must be at least 8")
        }
    }

    @Nested
    inner class RecaptchaToken {
        @Test
        fun `validates recaptchaToken for null`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    recaptchaToken = null
                )
            )
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("recaptchaToken is required")
            assertThat(violations.map { it.message }).contains("recaptchaToken must be set")
        }

        @Test
        fun `validates recaptchaToken for empty string`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    recaptchaToken = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("recaptchaToken must be set")
        }
    }

    @Nested
    inner class MarketingInformation {
        @Test
        fun `validates utmContent for length of characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    utmContent = "some info".repeat(210)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("utmContent cannot be longer than 200 characters")
        }

        @Test
        fun `validates utmCampaign for length of characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    utmCampaign = "some info".repeat(210)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("utmCampaign cannot be longer than 200 characters")
        }

        @Test
        fun `validates utmMedium for length of characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    utmMedium = "some info".repeat(210)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("utmMedium cannot be longer than 200 characters")
        }

        @Test
        fun `validates utmTerm for length of characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    utmTerm = "some info".repeat(210)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("utmTerm cannot be longer than 200 characters")
        }

        @Test
        fun `validates utmSource for length of characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    utmSource = "some info".repeat(210)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("utmSource cannot be longer than 200 characters")
        }
    }

    @Nested
    inner class OptionalFields {
        @Test
        fun `referral code cannot be longer than 50 characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    referralCode = StringUtils.repeat("X", 51)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Referral code cannot be longer than 50 characters")
        }

        @Test
        fun `analytics id cannot be longer than 100 characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    analyticsId = StringUtils.repeat("X", 101)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Analytics ID cannot be longer than 100 characters")
        }

    }
}
