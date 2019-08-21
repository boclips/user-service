package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.Platform
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            email = user.account.email,
            analyticsId = user.analyticsId?.value,
            organisationId = when (user.account.platform) {
                Platform.BoclipsForTeachers -> null
                is Platform.ApiCustomer -> user.account.platform.organisationId.value
            }
        )
    }
}
