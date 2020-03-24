package com.boclips.users.presentation.resources.converters

import com.boclips.users.api.response.SubjectResource
import com.boclips.users.api.response.TeacherPlatformAttributesResource
import com.boclips.users.api.response.user.UserResource
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.stereotype.Component

@Component
class UserConverter(
    private val userLinkBuilder: UserLinkBuilder
) {
    fun toUserResource(user: User, organisation: Organisation<*>?): UserResource {
        return UserResource(
            id = user.id.value,
            firstName = user.profile?.firstName,
            lastName = user.profile?.lastName,
            ages = user.profile?.ages,
            subjects = user.profile?.subjects?.map {
                SubjectResource(
                    it.id.value
                )
            },
            email = user.identity.email,
            analyticsId = user.analyticsId?.value,
            organisationAccountId = user.organisationId?.value,
            organisation = user.organisationId?.let {
                organisation?.let { orgAccount ->
                    OrganisationDetailsConverter().toResource(orgAccount.details)
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
                userLinkBuilder.accessRulesLink(UserId(user.id.value))
            ).map { it.rel.value() to it }.toMap()
        )
    }
}