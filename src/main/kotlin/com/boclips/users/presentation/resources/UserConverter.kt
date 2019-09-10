package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.User
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            ages = user.profile?.ages,
            subjects = user.profile?.subjects?.map { it.id.value },
            email = user.account.email,
            analyticsId = user.analyticsId?.value,
            country = user.profile?.country?.let { CountryResource(id = it.id, name = it.name, states = null) }
        )
    }
}
