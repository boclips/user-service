package com.boclips.users.application

import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.convertUserToCrmProfile
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class SynchronisationService(
    val marketingService: MarketingService,
    val sessionProvider: SessionProvider,
    val userImportService: UserImportService,
    val identityProvider: IdentityProvider,
    val userRepository: UserRepository
) {
    companion object : KLogging()

    fun synchroniseCrmProfiles() {
        val teacherUsers = userRepository.findAllTeachers()
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

    fun synchroniseUserAccounts() {
        val users = userRepository.findAll()

        val allUserIds = users.map { it.id }.toSet()
        logger.info { "Found ${allUserIds.size} users" }

        identityProvider.getIdentity().forEach { userAccount ->
            if (!allUserIds.contains(userAccount.id)) {
                userImportService.importFromIdentityProvider(listOf(userAccount.id))
                logger.info { "Import of user $userAccount completed" }
            }
        }
    }
}
