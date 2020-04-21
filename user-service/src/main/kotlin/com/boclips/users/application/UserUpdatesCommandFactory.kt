package com.boclips.users.application

import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserUpdate
import com.boclips.users.api.request.user.UpdateUserRequest
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Component

@Component
class UserUpdatesCommandFactory(private val subjectService: SubjectService) {
    fun buildCommands(
        updateUserRequest: UpdateUserRequest,
        organisation: Organisation<*>? = null
    ): List<UserUpdate> {
        return listOfNotNull(
            updateUserRequest.firstName?.let { UserUpdate.ReplaceFirstName(firstName = it) },
            updateUserRequest.lastName?.let { UserUpdate.ReplaceLastName(lastName = it) },
            updateUserRequest.subjects?.let { UserUpdate.ReplaceSubjects(subjects = convertSubjects(it)) },
            updateUserRequest.ages?.let { UserUpdate.ReplaceAges(ages = it) },
            updateUserRequest.hasOptedIntoMarketing?.let {
                UserUpdate.ReplaceHasOptedIntoMarketing(
                    hasOptedIntoMarketing = it
                )
            },
            updateUserRequest.referralCode?.let { UserUpdate.ReplaceReferralCode(referralCode = it) },
            updateUserRequest.utm?.let {
                UserUpdate.ReplaceMarketingTracking(
                    utmCampaign = it.campaign,
                    utmTerm = it.term,
                    utmMedium = it.medium,
                    utmContent = it.content,
                    utmSource = it.source
                )
            },
            updateUserRequest.role?.let { UserUpdate.ReplaceRole(role = it)},
            organisation?.let { UserUpdate.ReplaceOrganisation(it) },
            @Suppress("UNCHECKED_CAST")
            organisation?.let { if(it.details is School) UserUpdate.ReplaceProfileSchool(it as Organisation<School>) else null}
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
