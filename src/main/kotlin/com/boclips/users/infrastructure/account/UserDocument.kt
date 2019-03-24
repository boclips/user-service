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
    val referralCode: String?
) {
    companion object {
        fun from(account: Account) = UserDocument(
            id = account.id.value,
            activated = account.activated,
            subjects = account.subjects,
            analyticsId = account.analyticsId?.value,
            isReferral = account.isReferral,
            referralCode = account.referralCode
        )
    }

    fun toAccount() = Account(
        id = UserId(value = id),
        activated = activated,
        subjects = subjects,
        analyticsId = analyticsId?.let { AnalyticsId(value = it) },
        isReferral = isReferral?.let { it } ?: false,
        referralCode = referralCode?.let { it }
    )
}