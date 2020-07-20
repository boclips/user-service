package com.boclips.users.api.response.accessrule

open class AccessRulesResource(
    val _embedded: AccessRulesWrapper
)

data class AccessRulesWrapper(val accessRules: List<AccessRuleResource>)
