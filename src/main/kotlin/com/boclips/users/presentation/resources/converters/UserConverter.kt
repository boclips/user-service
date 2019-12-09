package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.presentation.resources.SubjectResource
import com.boclips.users.presentation.resources.TeacherPlatformAttributesResource
import com.boclips.users.presentation.resources.UserResource
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
            organisation = user.organisationAccountId?.let {
                organisationAccount?.let { orgAccount ->
                    OrganisationConverter().toResource(orgAccount.organisation)
                }
            },
            teacherPlatformAttributes = user.teacherPlatformAttributes?.let {
                TeacherPlatformAttributesResource(
                    shareCode = user.teacherPlatformAttributes.shareCode
                )
            }
        )
    }
}
