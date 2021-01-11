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
    private val createTeacher: CreateTeacher
) {

    operator fun invoke(createUserRequest: CreateUserRequest, currentUser: LoggedInUser?): User {
        return when (createUserRequest) {
            is CreateUserRequest.CreateTeacherRequest -> createTeacher(createUserRequest)
            is CreateUserRequest.CreateApiUserRequest -> validateAndCreateApiUser(currentUser, createUserRequest)
        }
    }

    private fun validateAndCreateApiUser(
        currentUser: LoggedInUser?,
        createUserRequest: CreateUserRequest.CreateApiUserRequest
    ): User {
        if (currentUser == null || !currentUser.hasRole(UserRoles.CREATE_API_USERS)) {
            throw PermissionDeniedException()
        }
        return createApiUser(createUserRequest)
    }
}
