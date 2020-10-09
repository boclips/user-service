package com.boclips.users.domain.service.events

import com.boclips.eventbus.events.contentpackage.ContentPackageUpdated
import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.domain.model.organisation.OrganisationUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ContentPackageRepositoryEventDecoratorTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var repository: ContentPackageRepositoryEventDecorator

    @Test
    fun `content package updated events get dispatched when content package is created`() {
        ContentPackageFactory
            .sample()
            .let(::saveContentPackage)
            .let(repository::replace)

        val events = eventBus.getEventsOfType(ContentPackageUpdated::class.java)
        Assertions.assertThat(events).hasSize(1)
    }
}
