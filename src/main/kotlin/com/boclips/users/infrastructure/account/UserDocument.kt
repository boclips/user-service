package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.analytics.MixpanelId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val activated: Boolean,
    val subjects: String?,
    val analyticsId: String?
) {
    companion object {
        fun from(account: Account) = UserDocument(
            id = account.id.value,
            activated = account.activated,
            subjects = account.subjects,
            analyticsId = account.analyticsId?.value
        )
    }

    fun toUser() = Account(
        id = AccountId(value = id),
        activated = activated,
        subjects = subjects,
        analyticsId = analyticsId?.let { MixpanelId(value = it) }
    )
}