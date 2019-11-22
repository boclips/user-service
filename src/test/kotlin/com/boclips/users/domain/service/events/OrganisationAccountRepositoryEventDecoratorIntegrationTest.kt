package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.service.OrganisationAccountTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.assertj.core.api.Assertions.assertThat

class OrganisationAccountRepositoryEventDecoratorIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var repository: OrganisationAccountRepositoryEventDecorator

    @Test
    fun `user updated events are dispatched when district is updated`() {
        val district = saveDistrict()
        val school = saveSchool(OrganisationFactory.school(district = district))
        saveUser(UserFactory.sample(organisationAccountId = school.id, account = AccountFactory.sample("u1")))
        saveUser(UserFactory.sample(organisationAccountId = school.id, account = AccountFactory.sample("u2")))
        saveUser(UserFactory.sample(organisationAccountId = null, account = AccountFactory.sample("u3")))

        repository.update(
            OrganisationAccountTypeUpdate(
                district.id,
                OrganisationAccountType.DESIGN_PARTNER
            )
        )

        val events = eventBus.getEventsOfType(UserUpdated::class.java)
        assertThat(events).hasSize(2)
    }

    @Test
    fun `user updated events are dispatched when school is updated`() {
        val school = saveSchool(OrganisationFactory.school(district = null))
        saveUser(UserFactory.sample(organisationAccountId = school.id, account = AccountFactory.sample("u1")))
        saveUser(UserFactory.sample(organisationAccountId = school.id, account = AccountFactory.sample("u2")))
        saveUser(UserFactory.sample(organisationAccountId = null, account = AccountFactory.sample("u3")))

        repository.update(
            OrganisationAccountTypeUpdate(
                school.id,
                OrganisationAccountType.DESIGN_PARTNER
            )
        )

        val events = eventBus.getEventsOfType(UserUpdated::class.java)
        assertThat(events).hasSize(2)
    }
}
