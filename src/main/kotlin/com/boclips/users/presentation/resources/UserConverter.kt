package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            analyticsId = user.analyticsId?.value,
            organisationId = user.associatedTo?.value
        )
    }
}
