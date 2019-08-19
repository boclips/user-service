package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserSource
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
            organisationId = when (user.account.associatedTo) {
                UserSource.Boclips -> null
                is UserSource.ApiClient -> user.account.associatedTo.organisationId.value
            }
        )
    }
}
