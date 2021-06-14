package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.User
import org.springframework.stereotype.Component
import com.boclips.security.utils.User as LoggedInUser

@Component
class CreateUser(
    private val createApiUser: CreateApiUser,
    private val createB2bUser: CreateB2bUser,
    private val createTeacher: CreateTeacher
) {

    operator fun invoke(createUserRequest: CreateUserRequest, currentUser: LoggedInUser?): User {
        return when (createUserRequest) {
            is CreateUserRequest.CreateApiUserRequest -> {
                validateUserHasRole(currentUser, UserRoles.CREATE_API_USERS)
                createApiUser(createUserRequest)
            }
            is CreateUserRequest.CreateB2bUserRequest -> {
                validateUserHasRole(currentUser, UserRoles.CREATE_B2B_USERS)
                createB2bUser(createUserRequest)
            }
            is CreateUserRequest.CreateTeacherRequest -> createTeacher(createUserRequest)
        }
    }

    private fun validateUserHasRole(currentUser: LoggedInUser?, role: String) {
        if (currentUser == null || !currentUser.hasRole(role)) throw PermissionDeniedException() else return
    }
}
