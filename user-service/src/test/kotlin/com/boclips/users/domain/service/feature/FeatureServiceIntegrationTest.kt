package com.boclips.users.domain.service.feature

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class FeatureServiceIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var featureService: FeatureService

    @Test
    fun `should return all features assigned to a user`() {
        val organisation = saveOrganisation(OrganisationFactory.district(
            features = mapOf(Pair(Feature.LTI_COPY_RESOURCE_LINK, true))
        ))
        val user = saveUser(UserFactory.sample(organisation = organisation))

        val features = featureService.getFeatures(user.id)
        assertTrue(features[Feature.LTI_COPY_RESOURCE_LINK]!!)
    }

    @Test
    fun `should return all features with default values when not specified`() {
        val organisation = saveOrganisation(OrganisationFactory.district())
        val user = saveUser(UserFactory.sample(organisation = organisation))

        val features = featureService.getFeatures(user.id)
        assertFalse(features[Feature.LTI_COPY_RESOURCE_LINK]!!)
    }

    @Test
    fun `should return all features with default values when user has no organisation`() {
        val user = saveUser(UserFactory.sample())

        val features = featureService.getFeatures(user.id)
        assertFalse(features[Feature.LTI_COPY_RESOURCE_LINK]!!)
    }

    @Test
    fun `should throw UserNotFoundException when no user found`() {
        assertThrows<UserNotFoundException> { featureService.getFeatures(UserId("non-existing-id")) }
    }
}
