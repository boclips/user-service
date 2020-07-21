package com.boclips.users.api.request

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class AccessRuleRequestValidationTest {
    @BeforeEach
    fun setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `validation fails for empty list of collectionIds`() {
        val request = AccessRuleRequest.IncludedCollections().apply {
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
        val request = AccessRuleRequest.IncludedCollections().apply {
            name = "The best contract ever"
            collectionIds = null
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails for empty types`() {
        val request = AccessRuleRequest.ExcludedVideoTypes().apply {
            name = "A video type contract"
            videoTypes = emptyList()
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    @Test
    fun `validation fails for empty channel ids`() {
        val request = AccessRuleRequest.IncludedChannels().apply {
            name = "A video type contract"
            channelIds = emptyList()
        }

        assertThat(validator.validate(request))
            .isNotEmpty
            .extracting("message")
            .contains("must not be empty")
    }

    lateinit var validator: Validator
}
