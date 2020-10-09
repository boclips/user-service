package com.boclips.users.domain.service.access

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ContentPackageRepository
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

    fun forOrganisation(organisation: Organisation?): List<AccessRule> {
        return organisation
            ?.deal
            ?.contentPackageId
            ?.let { contentPackageId ->
                contentPackageRepository.findById(contentPackageId)
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
