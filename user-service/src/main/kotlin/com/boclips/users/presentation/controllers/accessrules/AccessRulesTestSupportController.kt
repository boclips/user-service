package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.application.commands.CreateAccessRule
import com.boclips.users.application.commands.GetAccessRuleById
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.api.request.CreateAccessRuleRequest
import com.boclips.users.presentation.converters.AccessRuleConverter
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@BoclipsE2ETestSupport
@RestController
@RequestMapping("/v1/access-rules")
class AccessRuleTestSupportController(
    private val createAccessRule: CreateAccessRule,
    private val getAccessRuleById: GetAccessRuleById,
    private val accessRuleLinkBuilder: AccessRuleLinkBuilder,
    private val accessRuleConverter: AccessRuleConverter
) {
    @PostMapping
    fun insertAccessRule(@Valid @RequestBody request: CreateAccessRuleRequest): ResponseEntity<EntityModel<*>> {
        val createdAccessRule = createAccessRule(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, accessRuleLinkBuilder.self(createdAccessRule.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun fetchAccessRule(@PathVariable("id") id: String): AccessRuleResource {
        return accessRuleConverter.toResource(
            getAccessRuleById(id)
        )
    }
}
