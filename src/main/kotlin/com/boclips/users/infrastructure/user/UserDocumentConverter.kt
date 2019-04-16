package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.infrastructure.subjects.SubjectMapper

data class UserDocumentConverter(private val subjectMapper: SubjectMapper) {
    fun convertToUser(userDocument: UserDocument) = User(
        id = UserId(value = userDocument.id),
        activated = userDocument.activated,
        analyticsId = userDocument.analyticsId?.let { AnalyticsId(value = it) },
        subjects = userDocument.subjectIds.orEmpty().mapNotNull { id ->
            subjectMapper.getName(id)?.let {
                Subject(
                    id = SubjectId(value = id),
                    name = it
                )
            }
        },
        ageRange = userDocument.ageRange.orEmpty(),
        referralCode = userDocument.referralCode?.let { it },
        firstName = userDocument.firstName.orEmpty(),
        lastName = userDocument.lastName.orEmpty(),
        email = userDocument.email.orEmpty(),
        hasOptedIntoMarketing = userDocument.hasOptedIntoMarketing ?: true
    )
}