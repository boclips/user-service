package com.boclips.users.domain.model.contentpackage

import com.boclips.users.domain.service.UniqueId

data class AccessRuleId(val value: String) {

    companion object {
        operator fun invoke(): AccessRuleId {
            return AccessRuleId(value = UniqueId())
        }
    }
}
