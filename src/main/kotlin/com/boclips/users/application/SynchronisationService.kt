package com.boclips.users.application

import com.boclips.users.domain.service.AccountProvider
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
    val accountProvider: AccountProvider,
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

    fun synchroniseAccounts() {
        val allAccountIds = accountProvider.getAccounts().map { it.id }
        logger.info { "Found ${allAccountIds.size} accounts" }

        val allUserIds = userRepository.findAll().map { it.id }
        logger.info { "Found ${allUserIds.size} users" }

        val newUsers = allAccountIds - allUserIds
        logger.info { "Importing ${newUsers.size} users" }

        userImportService.importFromAccountProvider(newUsers)
        logger.info { "Import of ${newUsers.size} users completed" }
    }
}
