package com.boclips.users.application

import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.api.request.user.UpdateUserRequest
import org.springframework.stereotype.Component

@Component
class UserUpdatesCommandFactory(private val subjectService: SubjectService) {
    fun buildCommands(
        updateUserRequest: UpdateUserRequest,
        organisation: Organisation<*>? = null
    ): List<UserUpdateCommand> {
        return listOfNotNull(
            updateUserRequest.firstName?.let { UserUpdateCommand.ReplaceFirstName(firstName = it) },
            updateUserRequest.lastName?.let { UserUpdateCommand.ReplaceLastName(lastName = it) },
            updateUserRequest.subjects?.let { UserUpdateCommand.ReplaceSubjects(subjects = convertSubjects(it)) },
            updateUserRequest.ages?.let { UserUpdateCommand.ReplaceAges(ages = it) },
            updateUserRequest.hasOptedIntoMarketing?.let {
                UserUpdateCommand.ReplaceHasOptedIntoMarketing(
                    hasOptedIntoMarketing = it
                )
            },
            updateUserRequest.referralCode?.let { UserUpdateCommand.ReplaceReferralCode(referralCode = it) },
            updateUserRequest.utm?.let {
                UserUpdateCommand.ReplaceMarketingTracking(
                    utmCampaign = it.campaign,
                    utmTerm = it.term,
                    utmMedium = it.medium,
                    utmContent = it.content,
                    utmSource = it.source
                )
            },
            updateUserRequest.role?.let { UserUpdateCommand.ReplaceRole(role = it)},
            organisation?.let { UserUpdateCommand.ReplaceOrganisation(it) }
        )
    }

    private fun convertSubjects(subjects: List<String>): List<Subject> {
        return if (containsInvalidSubjects(subjects)) {
            throw InvalidSubjectException(subjects)
        } else {
            subjectService.getSubjectsById(subjects.map { SubjectId(value = it) })
        }
    }

    private fun containsInvalidSubjects(subjects: List<String>?) =
        !subjectService.allSubjectsExist(subjects.orEmpty().map { SubjectId(value = it) })
}
