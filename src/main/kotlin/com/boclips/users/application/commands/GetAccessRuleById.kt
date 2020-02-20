package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRuleById(private val accessRuleRepository: AccessRuleRepository) {
    operator fun invoke(id: String): AccessRule {
        val accessRuleId = AccessRuleId(id)
        return accessRuleRepository.findById(accessRuleId) ?: throw AccessRuleNotFoundException(id)
    }
}
