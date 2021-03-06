package com.boclips.users.application

import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.domain.service.access.AccessExpiryService
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.marketing.convertUserToCrmProfile
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.user.SessionProvider
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class SynchronisationService(
    val marketingService: MarketingService,
    val accessExpiryService: AccessExpiryService,
    val sessionProvider: SessionProvider,
    val userImportService: UserImportService,
    val identityProvider: IdentityProvider,
    val userRepository: UserRepository,
    val organisationRepository: OrganisationRepository
) {
    companion object : KLogging()

    fun synchroniseCrmProfiles() {
        val teacherUsers = userRepository.findAllTeachers()
            .filter { user -> !user.identity.isBoclipsEmployee() && accessExpiryService.userHasAccess(user) }
        logger.info { "Found ${teacherUsers.size} active teacher users to be synchronised" }

        val allCrmProfiles = teacherUsers
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map convertUserToCrmProfile(
                    user,
                    sessions
                )
            }
            .filterNotNull()

        logger.info { "Updating ${allCrmProfiles.size} profiles" }
        marketingService.updateProfile(allCrmProfiles)
        logger.info { "Updated ${allCrmProfiles.size} profiles" }
    }

    fun synchroniseUserAccounts() {
        val users = userRepository.findAll()

        val allUserIds = users.map { it.id }.toSet()
        logger.info { "Found ${allUserIds.size} users" }

        identityProvider.getAllIdentityIds().forEach { id ->
            if (!allUserIds.contains(id)) {
                userImportService.importFromIdentityProvider(listOf(id))
                logger.info { "Import of user with id: $id completed" }
            }
        }
    }

    fun synchroniseUsersOrganisations() {
        val organisations = organisationRepository.findAll()
        logger.info { "Found ${organisations.size} organisations" }

        organisations.forEach { org ->
            val users = userRepository.findAllByOrganisationId(org.id)
            logger.info { "Updating ${org.name} organisation, found ${users.size} users" }
            users.forEach { user ->
                userRepository.update(user, UserUpdate.ReplaceOrganisation(org))
            }
        }
    }
}
