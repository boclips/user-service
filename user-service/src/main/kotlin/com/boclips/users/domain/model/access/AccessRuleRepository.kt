package com.boclips.users.domain.model.access

interface AccessRuleRepository {
    fun findById(id: AccessRuleId): AccessRule?
    fun findByIds(accessRuleIds: List<AccessRuleId>): List<AccessRule>
    fun findAll(): List<AccessRule>
    fun findAllByName(name: String): List<AccessRule>
    fun <T : AccessRule> save(accessRule: T): T
}
