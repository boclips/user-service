package com.boclips.users.application

import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.user.SessionProvider
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.domain.service.marketing.convertUserToCrmProfile
import mu.KLogging
import org.apache.commons.validator.routines.EmailValidator
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

        identityProvider.getIdentity().forEach { userAccount ->
            if (!allUserIds.contains(userAccount.id)) {
                userImportService.importFromIdentityProvider(listOf(userAccount.id))
                logger.info { "Import of user $userAccount completed" }
            }
        }
    }

    fun synchroniseMoeAccountEmails() {
        val users = userRepository.findAll()

        val allUserIds = users.map { it.id }.toSet()
        logger.info { "Found ${allUserIds.size} users" }

        identityProvider.getIdentity().forEach { userAccount ->
            userAccount.firstName?.let { firstName ->
                if (userAccount.roles.contains("ROLE_MOE_UAE") && EmailValidator.getInstance().isValid(firstName)) {
                    userRepository.findById(userAccount.id)?.let { userToUpdate ->
                        userRepository.update(userToUpdate, UserUpdate.ReplaceEmail(email = firstName))
                    }
                    logger.info { "setting of $userAccount email completed" }
                }
            }
        }
    }
}

