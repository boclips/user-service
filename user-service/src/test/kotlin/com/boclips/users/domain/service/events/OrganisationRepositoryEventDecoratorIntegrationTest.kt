package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.domain.service.OrganisationDomainUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var repository: OrganisationRepositoryEventDecorator

    @Test
    fun `organisation updated event gets dispatched when organisation is updated`() {
        val organisation = saveSchool()

        repository.updateOne(OrganisationDomainUpdate(id = organisation.id, domain = "newdomain.com"))

        val events = eventBus.getEventsOfType(OrganisationUpdated::class.java)
        assertThat(events).hasSize(1)
    }

    @Test
    fun `organisation updated events get dispatched when parent organisation is updated`() {
        val parent = saveDistrict()
        val child = saveSchool(school = OrganisationDetailsFactory.school(district = parent))

        repository.updateOne(OrganisationDomainUpdate(id = parent.id, domain = "newdomain.com"))

        val events = eventBus.getEventsOfType(OrganisationUpdated::class.java)
        assertThat(events).hasSize(2)
        assertThat(events.map { it.organisation.id }).containsExactlyInAnyOrder(parent.id.value, child.id.value)
    }
}
