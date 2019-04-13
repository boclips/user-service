package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.User
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
    val analyticsId: String?,
    val referralCode: String?,
    val hasOptedIntoMarketing: Boolean?
) {
    companion object {
        fun from(user: User) = UserDocument(
            id = user.id.value,
            activated = user.activated,
            subjectIds = user.subjects,
            analyticsId = user.analyticsId?.value,
            referralCode = user.referralCode,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            hasOptedIntoMarketing = user.hasOptedIntoMarketing
        )
    }

    fun toUser() = User(
        id = UserId(value = id),
        activated = activated,
        analyticsId = analyticsId?.let { AnalyticsId(value = it) },
        subjects = subjectIds.orEmpty(),
        referralCode = referralCode?.let { it },
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        email = email.orEmpty(),
        hasOptedIntoMarketing = hasOptedIntoMarketing ?: true
    )
}