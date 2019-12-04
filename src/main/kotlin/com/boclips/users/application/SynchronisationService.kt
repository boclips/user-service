package com.boclips.users.application

import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.UserUpdateCommand
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
        val allUserIds = userRepository.findAll().map { it.id }.toSet()
        logger.info { "Found ${allUserIds.size} users" }

        accountProvider.getAccounts().forEach { account ->
            if (!allUserIds.contains(account.id)) {
                userImportService.importFromAccountProvider(listOf(account.id))
                logger.info { "Import of user $account completed" }
            }
        }
    }

    fun migrateCreatedAt() {
        accountProvider.getAccounts().forEach { account ->
            if (account.createdAt == null) {
                logger.warn { "Account createdAt missing for: ${account.id}" }
                return@forEach
            }

            userRepository.findById(account.id)?.let {
                if (it.account.createdAt != account.createdAt) {
                    logger.info { "Updating account created at for ${account.id}" }
                    userRepository.update(it, UserUpdateCommand.ReplaceAccountCreatedAt(account.createdAt), UserUpdateCommand.ReplaceHasLifetimeAccess(true))
                }
            } ?: logger.warn { "User cannot be found in userRepository, but exists in keycloak: ${account.id}" }
        }
    }
}
