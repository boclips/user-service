package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class UserTest {

    @Test
    fun `returns features assigned to organisation`() {
        // given
        val user = UserFactory.sample(organisation = OrganisationFactory.school(features = null))

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(Feature.DEFAULT_VALUES)
    }

    @Test
    fun `resolves features to default ones when organisation has no features configured`() {
        // given
        val user = UserFactory.sample(organisation = OrganisationFactory.school(features = null))

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(Feature.DEFAULT_VALUES)
    }

    @Test
    fun `resolves features to default ones when no organisation is assigned to a user`() {
        // given
        val user = UserFactory.sample(organisation = null)

        // when
        val features = user.features

        // then
        assertThat(features).isEqualTo(Feature.DEFAULT_VALUES)
    }
}
