package com.boclips.users.application

import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.presentation.requests.UpdateUserRequest
import org.springframework.stereotype.Component

@Component
class UserUpdatesConverter(private val subjectService: SubjectService) {
    fun convert(updateUserRequest: UpdateUserRequest, district: Organisation? = null): List<UserUpdateCommand> {
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
            updateUserRequest.country?.let { UserUpdateCommand.ReplaceCountry(country = Country.fromCode(it)) },
            updateUserRequest.state?.let { UserUpdateCommand.ReplaceState(state = State.fromCode(it)) },
            updateUserRequest.schoolName?.let { UserUpdateCommand.ReplaceSchool(school = it) },
            district?.let { UserUpdateCommand.ReplaceOrganisationId(organisationId = it.id) }
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