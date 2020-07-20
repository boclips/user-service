package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import com.boclips.users.application.commands.GetAccessRules
import com.boclips.users.presentation.converters.AccessRuleConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/access-rules")
class AccessRulesController(
    private val getAccessRules: GetAccessRules,
    private val accessRuleConverter: AccessRuleConverter
) {
    @GetMapping
    fun retrieveAccessRules(): AccessRulesResource {
        return AccessRulesResource(
            AccessRulesWrapper(
                getAccessRules().map { accessRuleConverter.toResource(it) }
            )
        )
    }
}
