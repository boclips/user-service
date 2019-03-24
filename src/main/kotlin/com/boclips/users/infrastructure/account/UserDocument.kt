package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.analytics.AnalyticsId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val activated: Boolean,
    val subjects: String?,
    val analyticsId: String?,
    val isReferral: Boolean?,
    val referralCode: String?,
    val firstName: String,
    val lastName: String,
    val email: String
) {
    companion object {
        fun from(account: Account) = UserDocument(
            id = account.id.value,
            activated = account.activated,
            subjects = account.subjects,
            analyticsId = account.analyticsId?.value,
            isReferral = !account.referralCode.isNullOrEmpty(),
            referralCode = account.referralCode,
            firstName = account.firstName,
            lastName = account.lastName,
            email = account.email
        )
    }

    fun toAccount() = Account(
        id = UserId(value = id),
        activated = activated,
        subjects = subjects,
        analyticsId = analyticsId?.let { AnalyticsId(value = it) },
        referralCode = referralCode?.let { it },
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}