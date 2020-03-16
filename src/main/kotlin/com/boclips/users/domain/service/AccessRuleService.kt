package com.boclips.users.domain.service

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.organisation.Organisation
import org.springframework.stereotype.Service

@Service
class AccessRuleService(
    private val contentPackageRepository: ContentPackageRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    fun forOrganisation(organisation: Organisation<*>?): List<AccessRule> {
        return organisation
            ?.contentPackageId
            ?.let { contentPackageId ->
                contentPackageRepository.findById(contentPackageId)
            }?.let { contentPackage ->
                contentPackage.accessRuleIds.mapNotNull { rule -> accessRuleRepository.findById(rule) }
            } ?: emptyList()
    }
}
