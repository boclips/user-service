package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val activated: Boolean,
    val subjectIds: List<String>?,
    val ageRange: List<Int>?,
    val analyticsId: String?,
    val referralCode: String?,
    val hasOptedIntoMarketing: Boolean?
) {
    companion object {
        fun from(user: User) = UserDocument(
            id = user.id.value,
            activated = user.activated,
            subjectIds = user.subjects.map { it.id.value },
            ageRange = user.ageRange,
            analyticsId = user.analyticsId?.value,
            referralCode = user.referralCode,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            hasOptedIntoMarketing = user.hasOptedIntoMarketing
        )
    }
}