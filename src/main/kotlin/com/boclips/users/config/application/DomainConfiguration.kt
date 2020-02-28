package com.boclips.users.config.application

import com.boclips.eventbus.EventBus
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.events.EventConverter
import com.boclips.users.domain.service.events.OrganisationRepositoryEventDecorator
import com.boclips.users.domain.service.events.UserRepositoryEventDecorator
import com.boclips.users.infrastructure.organisation.MongoOrganisationRepository
import com.boclips.users.infrastructure.user.MongoUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DomainConfiguration(
        private val eventBus: EventBus,
        private val mongoUserRepository: MongoUserRepository,
        private val mongoOrganisationRepository: MongoOrganisationRepository
) {
    @Primary
    @Bean
    fun userRepository(): UserRepository {
        return UserRepositoryEventDecorator(
            mongoUserRepository,
            eventConverter(),
            eventBus
        )
    }

    @Primary
    @Bean
    fun organisationAccountRepository(): OrganisationRepository {
        return OrganisationRepositoryEventDecorator(
            repository = mongoOrganisationRepository,
            eventBus = eventBus,
            eventConverter = eventConverter(),
            userRepository = mongoUserRepository
        )
    }

    @Bean
    fun eventConverter(): EventConverter {
        return EventConverter(mongoOrganisationRepository)
    }
}
