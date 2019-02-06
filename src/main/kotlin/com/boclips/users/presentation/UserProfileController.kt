package com.boclips.users.presentation

import com.boclips.users.application.GetAllUsers
import com.boclips.users.application.GetAllUsersInCsvFormat
import com.boclips.users.application.UserActions
import com.boclips.users.presentation.users.UserResource
import com.boclips.users.presentation.users.UsersResource
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserProfileController(
        private val userActions: UserActions,
        private val getAllUsers: GetAllUsers,
        private val getAllUsersInCsvFormat: GetAllUsersInCsvFormat
) {

    @PostMapping
    fun activateUser() = userActions.activateUser()

    @GetMapping("/{id}")
    fun getUserProfile(): Nothing? = null

    @GetMapping(produces = ["application/json"])
    fun getUsers(): ResponseEntity<UsersResource> =
            ResponseEntity(
                    UsersResource(users = getAllUsers()),
                    HttpStatus.OK
            )


    @GetMapping(produces = ["text/csv"])
    fun getUsersCsv(): ResponseEntity<String> {
        return ResponseEntity(
                getAllUsersInCsvFormat().joinToString("\n"),
                HttpStatus.OK
        )
    }
}


