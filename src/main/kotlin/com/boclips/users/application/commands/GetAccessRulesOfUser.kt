package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRulesOfUser(
    private val organisationRepository: OrganisationRepository,
    private val accessRuleRepository: AccessRuleRepository,
    private val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(userId: UserId): List<AccessRule> {
        return findUser(userId)
            ?.accessRuleIds?.mapNotNull(accessRuleRepository::findById)
            ?: emptyList()
    }

    private fun findUser(userId: UserId): Organisation<*>? {
        return getOrImportUser(userId).organisationId?.let {
            organisationRepository.findOrganisationById((it))
        }
    }
}
