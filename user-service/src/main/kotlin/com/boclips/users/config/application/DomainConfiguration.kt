package com.boclips.users.config.application

import com.boclips.eventbus.EventBus
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.events.EventConverter
import com.boclips.users.domain.service.events.OrganisationRepositoryEventDecorator
import com.boclips.users.domain.service.events.UserRepositoryEventDecorator
import com.boclips.users.domain.service.organisation.resolvers.FallbackOrganisationResolver
import com.boclips.users.domain.service.organisation.resolvers.OrganisationResolver
import com.boclips.users.domain.service.organisation.resolvers.OrganisationResolverChain
import com.boclips.users.domain.service.organisation.RoleBasedOrganisationResolver
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
            organisationRepository(),
            eventConverter(),
            eventBus
        )
    }

    @Primary
    @Bean
    fun organisationRepository(): OrganisationRepository {
        return OrganisationRepositoryEventDecorator(
            repository = mongoOrganisationRepository,
            eventBus = eventBus,
            eventConverter = eventConverter()
        )
    }

    @Bean
    fun eventConverter(): EventConverter {
        return EventConverter()
    }

    @Bean
    fun organisationResolver(): OrganisationResolver {
        val roleBasedResolver = RoleBasedOrganisationResolver(mongoOrganisationRepository)
        val fallbackResolver =
            FallbackOrganisationResolver(
                mongoOrganisationRepository
            )
        return OrganisationResolverChain(
            listOf(roleBasedResolver, fallbackResolver)
        )
    }
}
