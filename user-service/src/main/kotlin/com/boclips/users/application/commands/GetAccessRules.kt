package com.boclips.users.application.commands

import com.boclips.users.application.model.AccessRuleFilter
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRules(
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(filter: AccessRuleFilter): List<AccessRule> {
        return if (filter.name == null) {
            accessRuleRepository.findAll()
        } else {
            accessRuleRepository.findAllByName(filter.name)
        }
    }
}
