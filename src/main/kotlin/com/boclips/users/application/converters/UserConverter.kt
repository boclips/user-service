package com.boclips.users.application.converters

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.SubjectResource
import com.boclips.users.presentation.resources.UserResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.stereotype.Component

@Component
class UserConverter {
    fun toUserResource(user: User, organisationAccount: OrganisationAccount<*>?): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            ages = user.profile?.ages,
            subjects = user.profile?.subjects?.map { SubjectResource(it.id.value) },
            email = user.account.email,
            analyticsId = user.analyticsId?.value,
            organisationAccountId = user.organisationAccountId?.value,
            country = user.profile?.country?.let {
                CountryResource(
                    name = it.name,
                    id = it.id,
                    states = null
                )
            },
            state = user.profile?.state?.let{
                StateResource(
                    id = it.id,
                    name = it.name
                )
            },
            organisation = user.organisationAccountId?.let {
                organisationAccount?.let { orgAccount ->
                    OrganisationResource(
                        name = orgAccount.organisation.name,
                        state = orgAccount.organisation.state?.let {
                            StateResource(
                                name = it.name,
                                id = it.id
                            )
                        },
                        country = orgAccount.organisation.country?.let {
                            CountryResource(
                                name = it.name,
                                id = it.id,
                                states = null
                            )
                        }
                    )
                }
            }
        )
    }
}
