package com.boclips.users.application

import com.boclips.users.application.commands.GenerateTeacherShareCode
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
    val userRepository: UserRepository,
    val generateTeacherShareCode: GenerateTeacherShareCode
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
        val users = userRepository.findAll()

        val allUserIds = users.map { it.id }.toSet()
        logger.info { "Found ${allUserIds.size} users" }

        accountProvider.getAccounts().forEach { account ->
            if (!allUserIds.contains(account.id)) {
                userImportService.importFromAccountProvider(listOf(account.id))
                logger.info { "Import of user $account completed" }
            }
        }

        //TODO remove after shareCode migration
        users.forEach { user ->
            userRepository.update(user, UserUpdateCommand.ReplaceShareCode(generateTeacherShareCode()))
        }
    }
}
