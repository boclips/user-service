package com.boclips.users.application

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.requests.CreateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class CreateUser(
    private val userService: UserService,
    private val customerManagementProvider: CustomerManagementProvider
) {
    companion object : KLogging()

    operator fun invoke(createUserRequest: CreateUserRequest): User {
        val newUser = NewUser(
            firstName = createUserRequest.firstName!!,
            lastName = createUserRequest.lastName!!,
            email = createUserRequest.email!!,
            password = createUserRequest.password!!,
            analyticsId = AnalyticsId(value = createUserRequest.analyticsId.orEmpty()),
            subjects = createUserRequest.subjects.orEmpty(),
            referralCode = createUserRequest.referralCode.orEmpty()
        )
        val createdUser = userService.createUser(newUser = newUser)

        tryToUpdateCRM(createdUser)

        return createdUser
    }

    private fun tryToUpdateCRM(createdUser: User) {
        try {
            customerManagementProvider.update(users = listOf(createdUser))
        } catch (ex: Exception) {
            logger.error { "Could not update user ${createdUser.userId.value} as a contact on HubSpot" }
        }
    }
}