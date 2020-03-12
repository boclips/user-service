package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.service.OrganisationTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.assertj.core.api.Assertions.assertThat

class OrganisationRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var repository: OrganisationRepositoryEventDecorator

    @Test
    fun `user updated events are dispatched when district is updated`() {
        val district = saveDistrict()
        val school = saveSchool(OrganisationDetailsFactory.school(district = district))
        saveUser(UserFactory.sample(organisationId = school.id, identity = IdentityFactory.sample("u1")))
        saveUser(UserFactory.sample(organisationId = school.id, identity = IdentityFactory.sample("u2")))
        saveUser(UserFactory.sample(organisationId = null, identity = IdentityFactory.sample("u3")))

        repository.updateOne(
            OrganisationTypeUpdate(
                district.id,
                DealType.DESIGN_PARTNER
            )
        )

        val events = eventBus.getEventsOfType(UserUpdated::class.java)
        assertThat(events).hasSize(2)
    }

    @Test
    fun `user updated events are dispatched when school is updated`() {
        val school = saveSchool(OrganisationDetailsFactory.school(district = null))
        saveUser(UserFactory.sample(organisationId = school.id, identity = IdentityFactory.sample("u1")))
        saveUser(UserFactory.sample(organisationId = school.id, identity = IdentityFactory.sample("u2")))
        saveUser(UserFactory.sample(organisationId = null, identity = IdentityFactory.sample("u3")))

        repository.updateOne(
            OrganisationTypeUpdate(
                school.id,
                DealType.DESIGN_PARTNER
            )
        )

        val events = eventBus.getEventsOfType(UserUpdated::class.java)
        assertThat(events).hasSize(2)
    }
}
