package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.service.AccessRuleService
import org.springframework.stereotype.Service

@Service
class GetAccessRulesOfUser(
    private val getOrImportUser: GetOrImportUser,
    private val accessRuleService: AccessRuleService
) {
    operator fun invoke(userId: String): List<AccessRule> {
        val user = getOrImportUser(UserId(value = userId))

        return accessRuleService.forOrganisation(user.organisation)
    }
}
