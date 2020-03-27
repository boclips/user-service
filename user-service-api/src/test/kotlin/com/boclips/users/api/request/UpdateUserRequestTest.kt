package com.boclips.users.api.request

import com.boclips.users.api.factories.UpdateUserRequestFactory
import com.boclips.users.api.request.user.MarketingTrackingRequest
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
    inner class Profile {
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

        @Test
        fun `validates role for value`() {
            val violations =
                validator.validate(
                    UpdateUserRequestFactory.sample(
                        role = "INVALID"
                    )
                )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Role must be TEACHER, PARENT, SCHOOLADMIN, or OTHER")
        }
    }

    @Nested
    inner class AgeRanges {
        @Test
        fun `empty age range is valid`() {
            val violations = validator.validate(UpdateUserRequestFactory.sample(ages = emptyList()))

            assertThat(violations).hasSize(0)
        }

        @Test
        fun `null age range is valid`() {
            val violations = validator.validate(UpdateUserRequestFactory.sample(ages = null))

            assertThat(violations).hasSize(0)
        }

        @Test
        fun `age ranges cannot be more than 18 years`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    ages = (0..100).toList()
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have more than 99 ages")
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
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `validates subjects for null`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    subjects = null
                )
            )
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `subjects cannot store more than 50 subjects`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    subjects = (0..51).map { "subject" }
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Cannot have more than 50 subjects")
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
    inner class School {
        @Test
        fun `validates school for empty string`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    schoolName = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("School name must be between 1 and 200 characters")
        }

        @Test
        fun `validates school for length`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    schoolName = "B".repeat(201)
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("School name must be between 1 and 200 characters")
        }
    }

    @Nested
    inner class MarketingTracking {
        @Test
        fun `validates marketing tracking request fields for length`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    utm = MarketingTrackingRequest(
                        campaign = "A".repeat(201),
                        term = "A".repeat(201),
                        medium = "A".repeat(201),
                        content = "A".repeat(201),
                        source = "A".repeat(201)
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

    @Nested
    inner class UsStates {
        @Test
        fun `null state are valid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(state = null)
            )
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `2 letter state is valid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(state = "CA")
            )
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `empty state are invalid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(state = "")
            )

            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Invalid USA state code")
        }

        @Test
        fun `random characters are invalid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(state = "CAL")
            )

            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Invalid USA state code")
        }
    }

    @Nested
    inner class Country {
        @Test
        fun `null country are valid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(country = null)
            )
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `3 letter country is valid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(country = "USA")
            )
            assertThat(violations).hasSize(0)
        }

        @Test
        fun `empty country are invalid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(country = "")
            )

            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Country must be 3 characters")
        }

        @Test
        fun `country longer than 3 characters are invalid`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(country = "USAB")
            )

            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Country must be 3 characters")
        }
    }
}
