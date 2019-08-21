package com.boclips.users.presentation.requests

import com.boclips.users.testsupport.factories.UpdateUserRequestFactory
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class UpdateUserRequestTest {

    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()
    }

    @Test
    fun `validates a valid request`() {
        val validRequest = UpdateUserRequestFactory.sample()
        val violations = validator.validate(validRequest)
        assertThat(violations).hasSize(0)
    }

    @Nested
    inner class Names {
        @Test
        fun `validates first firstName for empty string`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    firstName = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("First name must be between 1 and 200 characters")
        }

        @Test
        fun `validates firstName for length`() {
            val violations =
                validator.validate(
                    UpdateUserRequestFactory.sample(
                        firstName = "X".repeat(201)
                    )
                )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("First name must be between 1 and 200 characters")
        }

        @Test
        fun `validates first lastName for empty string`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    lastName = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Last name must be between 1 and 200 characters")
        }

        @Test
        fun `validates lastName for length`() {
            val violations =
                validator.validate(
                    UpdateUserRequestFactory.sample(
                        lastName = "X".repeat(201)
                    )
                )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Last name must be between 1 and 200 characters")
        }
    }

    @Nested
    inner class AgeRanges {
        @Test
        fun `validates age ranges for empty list`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    ages = emptyList()
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have less than 1 or more than 99 ages")
        }

        @Test
        fun `age ranges cannot be more than 18 years`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    ages = (0..100).toList()
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have less than 1 or more than 99 ages")
        }
    }

    @Nested
    inner class Subjects {
        @Test
        fun `validates subjects for empty list`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    subjects = emptyList()
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have less than 1 or more than 50 subjects")
        }

        @Test
        fun `subjects cannot store more than 50 subjects`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    subjects = (0..51).map { "subject" }
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have less than 1 or more than 50 subjects")
        }
    }

    @Nested
    inner class ReferralCode {
        @Test
        fun `validates referral code for length`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    referralCode = "B".repeat(51)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Referral code cannot be longer than 50 characters")
        }
    }

    @Nested
    inner class MarketingTracking {
        @Test
        fun `validates marketing tracking request fields for length`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    marketingTrackingRequest = MarketingTrackingRequest(
                        utmCampaign = "A".repeat(201),
                        utmTerm = "A".repeat(201),
                        utmMedium = "A".repeat(201),
                        utmContent = "A".repeat(201),
                        utmSource = "A".repeat(201)
                    )
                )
            )
            assertThat(violations).hasSize(5)
            assertThat(violations.map { it.message }).contains("utmCampaign cannot be longer than 200 characters")
            assertThat(violations.map { it.message }).contains("utmTerm cannot be longer than 200 characters")
            assertThat(violations.map { it.message }).contains("utmSource cannot be longer than 200 characters")
            assertThat(violations.map { it.message }).contains("utmContent cannot be longer than 200 characters")
            assertThat(violations.map { it.message }).contains("utmMedium cannot be longer than 200 characters")
        }
    }
}
