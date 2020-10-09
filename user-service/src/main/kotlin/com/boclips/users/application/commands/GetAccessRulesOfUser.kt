package com.boclips.users.application.commands

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.service.access.AccessRuleService
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
