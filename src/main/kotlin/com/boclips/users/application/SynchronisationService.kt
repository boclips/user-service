package com.boclips.users.application

import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.convertUserToCrmProfile
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class SynchronisationService(
    val userService: UserService,
    val marketingService: MarketingService,
    val sessionProvider: SessionProvider,
    val userImportService: UserImportService,
    val identityProvider: IdentityProvider,
    val userRepository: UserRepository
) {
    companion object : KLogging()

    fun synchroniseCrmProfiles() {
        val teacherUsers = userService.findAllTeachers()
        logger.info { "Found ${teacherUsers.size} teacher users to be synchronised" }

        val allCrmProfiles = teacherUsers
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map convertUserToCrmProfile(user, sessions)
            }
            .filterNotNull()

        logger.info { "Updating ${allCrmProfiles.size} profiles" }
        marketingService.updateProfile(allCrmProfiles)
        logger.info { "Updated ${allCrmProfiles.size} profiles" }
    }

    fun synchroniseIdentities() {
        val allIdentityIds = identityProvider.getUsers().map { it.id }
        logger.info { "Found ${allIdentityIds.size} identities" }

        val allUserIds = userRepository.findAll().map { it.id }
        logger.info { "Found ${allUserIds.size} users" }

        val newUsers = allIdentityIds - allUserIds
        logger.info { "Importing ${newUsers.size} users" }

        userImportService.importFromIdentityProvider(newUsers)
        logger.info { "Import of ${newUsers.size} users completed" }
    }
}
