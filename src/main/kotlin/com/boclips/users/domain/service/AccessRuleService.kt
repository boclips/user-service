package com.boclips.users.domain.service

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.organisation.Organisation
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AccessRuleService(
    private val contentPackageRepository: ContentPackageRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    companion object : KLogging() {
        const val DEFAULT_CONTENT_PACKAGE_NAME = "Classroom"
    }

    fun forOrganisation(organisation: Organisation<*>?): List<AccessRule> {
        return organisation
            ?.contentPackageId
            ?.let { contentPackageId ->
                contentPackageRepository.findById(contentPackageId)
            }?.let { contentPackage ->
                lookupAccessRules(contentPackage)
            } ?: defaultAccessRules()
    }

    private fun defaultAccessRules(): List<AccessRule> {
        return contentPackageRepository.findByName(DEFAULT_CONTENT_PACKAGE_NAME)?.let {
            lookupAccessRules(it)
        } ?: emptyList<AccessRule>().also {
            logger.warn { "Cannot find default content package '${DEFAULT_CONTENT_PACKAGE_NAME}'." }
        }
    }

    private fun lookupAccessRules(contentPackage: ContentPackage): List<AccessRule> {
        return contentPackage.accessRuleIds.mapNotNull { rule -> accessRuleRepository.findById(rule) }
    }
}
