package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.resources.SubjectResource
import com.boclips.users.presentation.resources.TeacherPlatformAttributesResource
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class UserConverter(val userLinkBuilder: UserLinkBuilder) {
    fun toUserResource(user: User, organisationAccount: Account<*>?): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            ages = user.profile?.ages,
            subjects = user.profile?.subjects?.map { SubjectResource(it.id.value) },
            email = user.identity.email,
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
            },
            _links = listOfNotNull(
                userLinkBuilder.profileSelfLink(UserId(user.id.value)),
                userLinkBuilder.profileLink(UserId(user.id.value)),
                userLinkBuilder.contractsLink(UserId(user.id.value))
            ).map { it.rel.value() to it }.toMap()
        )
    }
}
