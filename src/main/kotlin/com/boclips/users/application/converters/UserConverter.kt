package com.boclips.users.application.converters

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.UserResource
import com.boclips.users.presentation.resources.school.CountryResource
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User, organisationAccount: OrganisationAccount<*>?): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            ages = user.profile?.ages,
            subjects = user.profile?.subjects?.map { it.id.value },
            email = user.account.email,
            analyticsId = user.analyticsId?.value,
            country = user.profile?.country?.let { CountryResource(id = it.id, name = it.name, states = null) },
            organisation = user.organisationAccountId?.let {
                organisationAccount?.let {
                    OrganisationResource(
                        name = organisationAccount.organisation.name,
                        state = organisationAccount.organisation.state?.name,
                        country = organisationAccount.organisation.country?.name
                    )
                }
            }
        )
    }
}
