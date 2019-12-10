package com.boclips.users.presentation.requests

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class CreateContractRequestValidationTest {
    @BeforeEach
    fun setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `validation fails for empty name`() {
        val request = CreateContractRequest.SelectedCollections().apply {
            name = ""
            collectionIds = listOf("A", "B", "C")
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails for null name`() {
        val request = CreateContractRequest.SelectedCollections().apply {
            name = null
            collectionIds = listOf("A", "B", "C")
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails for empty list of collectionIds`() {
        val request = CreateContractRequest.SelectedCollections().apply {
            name = "The best contract ever"
            collectionIds = emptyList()
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails for null list of collectionIds`() {
        val request = CreateContractRequest.SelectedCollections().apply {
            name = "The best contract ever"
            collectionIds = null
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    lateinit var validator: Validator
}