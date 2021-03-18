package com.boclips.users.domain.service.access

import com.boclips.security.utils.Client
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.Organisation
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AccessRuleService(
    private val contentPackageRepository: ContentPackageRepository
) {
    companion object : KLogging() {
        const val DEFAULT_CONTENT_PACKAGE_NAME = "Classroom"
    }

    fun forOrganisation(organisation: Organisation?, client: String? = null): List<AccessRule> {
        val contentAccess = organisation?.deal?.contentAccess

        val contentPackageId = when (contentAccess) {
            is ContentAccess.ClientBasedAccess -> client?.let { contentAccess.clientAccess[Client.Teachers] }
            is ContentAccess.SimpleAccess -> contentAccess.id
            else -> null
        }

        return contentPackageId
            ?.let {
                contentPackageRepository.findById(it)
            }?.let { contentPackage ->
                contentPackage.accessRules
            } ?: defaultAccessRules()
    }

    private fun defaultAccessRules(): List<AccessRule> {
        return contentPackageRepository.findByName(DEFAULT_CONTENT_PACKAGE_NAME)?.let {
            it.accessRules
        } ?: emptyList<AccessRule>().also {
            logger.warn { "Cannot find default content package '$DEFAULT_CONTENT_PACKAGE_NAME'." }
        }
    }
}
