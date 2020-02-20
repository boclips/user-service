package com.boclips.users.domain.service

import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.accessrules.AccessRuleId

interface AccessRuleRepository {
    fun findById(id: AccessRuleId): AccessRule?
    fun findAll(): List<AccessRule>
    fun findAllByName(name: String): List<AccessRule>
    fun save(accessRule: AccessRule)
}
