package com.boclips.users.presentation.controllers

import com.boclips.security.utils.UserExtractor
import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.api.request.user.UpdateUserRequest
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import com.boclips.users.api.response.user.UserResource
import com.boclips.users.application.commands.CreateUser
import com.boclips.users.application.commands.GetAccessRulesOfUser
import com.boclips.users.application.commands.GetSelfUser
import com.boclips.users.application.commands.GetUser
import com.boclips.users.application.commands.IsUserActive
import com.boclips.users.application.commands.UpdateUser
import com.boclips.users.application.commands.ValidateShareCode
import com.boclips.users.application.exceptions.ApiUserAlreadyExistsException
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.presentation.converters.AccessRuleConverter
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.projections.WithProjection
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserController(
    private val createUser: CreateUser,
    private val updateUser: UpdateUser,
    private val getUser: GetUser,
    private val userLinkBuilder: UserLinkBuilder,
    private val withProjection: WithProjection,
    private val validateShareCode: ValidateShareCode,
    private val isUserActive: IsUserActive,
    private val accessRuleConverter: AccessRuleConverter,
    private val getAccessRulesOfUser: GetAccessRulesOfUser,
    private val getSelfUser: GetSelfUser
) {

    @PostMapping
    fun createAUser(@Valid @RequestBody createUserRequest: CreateUserRequest?): ResponseEntity<EntityModel<*>> {
        val user = try {
            createUser(createUserRequest!!, UserExtractor.getCurrentUser())
        } catch (e: ApiUserAlreadyExistsException) {
            return ResponseEntity.noContent().build()
        }

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, userLinkBuilder.newUserProfileLink(user.id)?.href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateAUser(
        @PathVariable id: String,
        @Valid @RequestBody updateUserRequest: UpdateUserRequest
    ): ResponseEntity<MappingJacksonValue> {
        updateUser(id, updateUserRequest)
        return getAUser(id)
    }

    @GetMapping("/_self")
    fun getSelf(): ResponseEntity<MappingJacksonValue> {
        return ResponseEntity(
            withProjection(getSelfUser()),
            HttpHeaders(),
            HttpStatus.OK
        )
    }

    @GetMapping("/{id}")
    fun getAUser(@PathVariable id: String?): ResponseEntity<MappingJacksonValue> {
        val userResource: UserResource = getUser(id!!)
        val headers = HttpHeaders()

        return ResponseEntity(
            withProjection(userResource),
            headers,
            HttpStatus.OK
        )
    }

    @GetMapping("/{id}/access-rules")
    fun fetchAccessRulesOfUser(@PathVariable id: String?, @RequestParam(name = "client", required = false) client: String?): AccessRulesResource {
        val userId = UserId(id!!)
        return AccessRulesResource(
            _embedded = AccessRulesWrapper(
                getAccessRulesOfUser(userId.value, client).map { accessRuleConverter.toResource(it) }
            )
        )
    }

    @GetMapping("/{id}/shareCode/{shareCode}")
    fun getShareCode(@PathVariable id: String?, @PathVariable shareCode: String?): ResponseEntity<Any> =
        if (validateShareCode(id!!, shareCode!!)) {
            ResponseEntity.status(HttpStatus.OK).build()
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

    @GetMapping("/{id}/active")
    fun getIsUserActive(@PathVariable id: String?): ResponseEntity<Any> =
        if (isUserActive(id!!)) {
            ResponseEntity.status(HttpStatus.OK).build()
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
}
