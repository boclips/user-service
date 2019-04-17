package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.service.SubjectService

data class UserDocumentConverter(private val subjectService: SubjectService) {
    fun convertToUser(userDocument: UserDocument) = User(
        id = UserId(value = userDocument.id),
        activated = userDocument.activated,
        analyticsId = userDocument.analyticsId?.let { AnalyticsId(value = it) },
        subjects = subjectService.getSubjectsById(userDocument.subjectIds.orEmpty().map { SubjectId(value = it) }),
        ageRange = userDocument.ageRange.orEmpty(),
        referralCode = userDocument.referralCode?.let { it },
        firstName = userDocument.firstName.orEmpty(),
        lastName = userDocument.lastName.orEmpty(),
        email = userDocument.email.orEmpty(),
        hasOptedIntoMarketing = userDocument.hasOptedIntoMarketing ?: true
    )
}