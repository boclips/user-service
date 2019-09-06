package com.boclips.users.presentation.requests

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class CreateOrganisationRequestValidationTest {
    @BeforeEach
    fun setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().validator
        request = CreateOrganisationRequest().apply {
            name = "Test Name"
            role = "ROLE_TEST_NAME"
        }
    }

    @Test
    fun `validation succeeds when given a valid object`() {
        assertThat(validator.validate(request)).isEmpty()
    }

    @Test
    fun `validation fails when given a blank name`() {
        request.name = ""

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails when given a null name`() {
        request.name = null

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails when given a blank role`() {
        request.role = ""

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails when given a null role`() {
        request.role = null

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    lateinit var validator: Validator
    lateinit var request: CreateOrganisationRequest
}
