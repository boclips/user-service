package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.application.commands.GetAccessRules
import com.boclips.users.application.model.AccessRuleFilter
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.AccessRuleResourcesHateoasWrapper
import com.boclips.users.presentation.hateoas.AccessRuleResourcesWrapper
import com.boclips.users.presentation.resources.converters.AccessRuleConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/v1/access-rules")
class AccessRulesController(
    private val getAccessRules: GetAccessRules,
    private val accessRuleConverter: AccessRuleConverter,
    private val accessRuleLinkBuilder: AccessRuleLinkBuilder
) {
    @GetMapping
    fun getAccessRules(@NotBlank @RequestParam(required = false) name: String?): AccessRuleResourcesHateoasWrapper {
        val filter = AccessRuleFilter(name = name)

        return AccessRuleResourcesHateoasWrapper(
            AccessRuleResourcesWrapper(
                getAccessRules(filter).map { accessRuleConverter.toResource(it) }
            ),
            listOfNotNull(
                accessRuleLinkBuilder.searchAccessRules(name = name, rel = "self")
            ).map { it.rel.value() to it }.toMap()
        )
    }
}
