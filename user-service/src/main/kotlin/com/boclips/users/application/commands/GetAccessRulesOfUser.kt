package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.service.AccessRuleService
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRulesOfUser(
    private val getOrImportUser: GetOrImportUser,
    private val accessRuleService: AccessRuleService,
    private val organisationRepository: OrganisationRepository
) {
    operator fun invoke(userId: String): List<AccessRule> {
        val user = getOrImportUser(UserId(value = userId))
        val organisation = user.organisationId?.let { organisationRepository.findOrganisationById(it) }

        return accessRuleService.forOrganisation(organisation)
    }
}
