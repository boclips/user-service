package com.boclips.users.presentation.controllers

import com.boclips.users.application.SynchronisationService
import com.boclips.users.application.commands.CreateTeacher
import com.boclips.users.application.commands.GetContentPackageOfUser
import com.boclips.users.application.commands.GetUser
import com.boclips.users.application.commands.UpdateUser
import com.boclips.users.application.commands.ValidateShareCode
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.projections.WithProjection
import com.boclips.users.presentation.requests.CreateTeacherRequest
import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.users.presentation.resources.ContentPackageResource
import com.boclips.users.presentation.resources.UserResource
import com.boclips.users.presentation.resources.converters.ContentPackageConverter
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
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserController(
    private val createTeacher: CreateTeacher,
    private val updateUser: UpdateUser,
    private val getUser: GetUser,
    private val userLinkBuilder: UserLinkBuilder,
    private val synchronisationService: SynchronisationService,
    private val withProjection: WithProjection,
    private val validateShareCode: ValidateShareCode,
    private val contentPackageConverter: ContentPackageConverter,
    private val getContentPackageOfUser: GetContentPackageOfUser
) {

    @PostMapping
    fun createAUser(@Valid @RequestBody createTeacherRequest: CreateTeacherRequest?): ResponseEntity<EntityModel<*>> {
        val user = createTeacher(createTeacherRequest!!)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, userLinkBuilder.newUserProfileLink(user.id)?.href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateAUser(@PathVariable id: String, @Valid @RequestBody updateUserRequest: UpdateUserRequest)
        : ResponseEntity<MappingJacksonValue> {
        updateUser(id, updateUserRequest)
        return getAUser(id)
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

    @GetMapping("/{id}/content-package")
    fun getContentPackageResourceOfUser(@PathVariable id: String?): ContentPackageResource {
        return getContentPackageOfUser(id!!).let { contentPackageConverter.toResource(it) }
    }

    @PostMapping("/sync")
    fun syncCrmContacts() {
        synchronisationService.synchroniseCrmProfiles()
    }

    @PostMapping("/sync-identities")
    fun syncAccounts() {
        synchronisationService.synchroniseUserAccounts()
    }

    @GetMapping("/{id}/shareCode/{shareCode}")
    fun checkUserShareCode(@PathVariable id: String?, @PathVariable shareCode: String?): ResponseEntity<Any> =
        if (validateShareCode(id!!, shareCode!!)) {
            ResponseEntity.status(HttpStatus.OK).build()
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
}
