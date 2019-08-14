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
        fun `validates first name for null`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    name = null
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Name is required")
        }

        @Test
        fun `validates first name for empty string`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    name = ""
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Name must be between 1 and 400 characters")
        }

        @Test
        fun `validates name for length`() {
            val violations =
                validator.validate(
                    UpdateUserRequestFactory.sample(
                        name = StringUtils.repeat("X", 401)
                    )
                )
            assertThat(violations).hasSize(1)
            assertThat(violations.first().message).isEqualTo("Name must be between 1 and 400 characters")
        }
    }

    @Nested
    inner class OptInMarketing {
        @Test
        fun `validates optInMarketing for null`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    hasOptedIntoMarketing = null
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Marketing preferences must not be null")
        }
    }

    @Nested
    inner class AgeRanges {

        @Test
        fun `validates age ranges for null`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    ages = null
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Ages are required")
        }

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
        fun `validates subjects for null`() {
            val violations = validator.validate(
                UpdateUserRequestFactory.sample(
                    subjects = null
                )
            )
            assertThat(violations).hasSize(1)
            assertThat(violations.map { it.message }).contains("Subjects are required")
        }

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
}
