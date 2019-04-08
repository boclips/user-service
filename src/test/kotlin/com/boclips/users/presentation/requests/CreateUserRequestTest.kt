package com.boclips.users.presentation.requests

import com.boclips.users.testsupport.CreateUserRequestFactory
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
                subjects = null,
                analyticsId = null,
                referralCode = null
            )
        )
        assertThat(violations).hasSize(0)
    }

    @Nested
    inner class FirstNames {
        @Test
        fun `validates first name for null`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(firstName = null))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("First name is required")
        }

        @Test
        fun `validates first name for empty string`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(firstName = ""))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("First name must be between 1 and 200 characters")
        }

        @Test
        fun `validates first name for length`() {
            val violations =
                validator.validate(CreateUserRequestFactory.sample(firstName = StringUtils.repeat("X", 201)))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("First name must be between 1 and 200 characters")
        }
    }

    @Nested
    inner class LastNames {
        @Test
        fun `validates last name for null`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(lastName = null))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Last name is required")
        }

        @Test
        fun `validates last name for empty string`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(lastName = ""))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Last name must be between 1 and 200 characters")
        }

        @Test
        fun `validates last name for length`() {
            val violations =
                validator.validate(CreateUserRequestFactory.sample(lastName = StringUtils.repeat("X", 201)))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Last name must be between 1 and 200 characters")
        }
    }

    @Nested
    inner class Emails {
        @Test
        fun `validates email for null`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(email = null))
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Email is required")
            assertThat(violations.map { it.message }).contains("Email must be set")
        }

        @Test
        fun `validates email for empty string`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(email = ""))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Email must be set")
        }

        @Test
        fun `validates email for format`() {
            val longEmail = "@test.com"
            val violations =
                validator.validate(CreateUserRequestFactory.sample(email = longEmail))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Email must be valid")
        }
    }

    @Nested
    inner class Passwords {
        @Test
        fun `validates password for null`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(password = null))
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Password is required")
            assertThat(violations.map { it.message }).contains("Password must be set")
        }

        @Test
        fun `validates password for empty string`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(password = ""))
            assertThat(violations).hasSize(2)
            assertThat(violations.map { it.message }).contains("Password length must be at least 8")
            assertThat(violations.map { it.message }).contains("Password must be set")
        }

        @Test
        fun `validates password for format`() {
            val shortPassword = "1234567"
            val violations = validator.validate(CreateUserRequestFactory.sample(password = shortPassword))
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Password length must be at least 8")
        }
    }

    @Nested
    inner class OptInMarketing {
        @Test
        fun `validates optInMarketing for null`() {
            val violations = validator.validate(CreateUserRequestFactory.sample(hasOptedIntoMarketing = null))
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Marketing preferences must not be null")
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

        @Test
        fun `subjects cannot be longer than 101 characters`() {
            val violations = validator.validate(
                CreateUserRequestFactory.sample(
                    subjects = StringUtils.repeat("X", 101)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Subjects cannot be longer than 100 characters")
        }
    }
}