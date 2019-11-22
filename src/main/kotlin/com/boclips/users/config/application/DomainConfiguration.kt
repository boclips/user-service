package com.boclips.users.config.application

import com.boclips.eventbus.EventBus
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.OrganisationAccountRepositoryEventDecorator
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserRepositoryEventDecorator
import com.boclips.users.infrastructure.organisation.MongoOrganisationAccountRepository
import com.boclips.users.infrastructure.user.MongoUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DomainConfiguration(
        private val eventBus: EventBus,
        private val mongoUserRepository: MongoUserRepository,
        private val mongoOrganisationAccountRepository: MongoOrganisationAccountRepository
) {
    @Primary
    @Bean
    fun userRepository(): UserRepository {
        return UserRepositoryEventDecorator(
            mongoUserRepository,
            organisationAccountRepository(),
            eventBus
        )
    }

    @Primary
    @Bean
    fun organisationAccountRepository(): OrganisationAccountRepository {
        return OrganisationAccountRepositoryEventDecorator(
            repository = mongoOrganisationAccountRepository,
            eventBus = eventBus,
            userRepository = mongoUserRepository
        )
    }
}
