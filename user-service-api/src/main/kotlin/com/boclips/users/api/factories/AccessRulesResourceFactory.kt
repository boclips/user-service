package com.boclips.users.api.factories

import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper

class AccessRulesResourceFactory {
    companion object {
        @JvmStatic
        fun sample(vararg accessRuleResource: AccessRuleResource): AccessRulesResource {
            return AccessRulesResource(
                _embedded = AccessRulesWrapper(accessRules = accessRuleResource.toList())
            )
        }
    }
}
