package com.boclips.users.domain.service

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.infrastructure.accessrules.AccessRuleDocument

interface AccessRuleRepository {
    fun findById(id: AccessRuleId): AccessRule?
    fun findAll(): List<AccessRule>
    fun findAllByName(name: String): List<AccessRule>
    fun save(accessRule: AccessRule): AccessRuleDocument
}
