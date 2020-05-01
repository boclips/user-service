package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceDomain
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var repository: OrganisationRepositoryEventDecorator

    @Test
    fun `organisation updated event gets dispatched when organisation is updated`() {
        val organisation = saveOrganisation(OrganisationFactory.school())

        repository.update(organisation.id, ReplaceDomain("newdomain.com"))

        val events = eventBus.getEventsOfType(OrganisationUpdated::class.java)
        assertThat(events).hasSize(1)
    }

    @Test
    fun `organisation updated events get dispatched when parent organisation is updated`() {
        val parent = saveOrganisation(OrganisationFactory.district())
        val child = saveOrganisation(OrganisationFactory.school(district = parent))

        repository.update(parent.id, ReplaceDomain("newdomain.com"))

        val events = eventBus.getEventsOfType(OrganisationUpdated::class.java)
        assertThat(events).hasSize(2)
        assertThat(events.map { it.organisation.id }).containsExactlyInAnyOrder(parent.id.value, child.id.value)
    }
}
