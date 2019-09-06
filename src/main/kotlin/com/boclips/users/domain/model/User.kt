package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationAccountId

data class User(
    val account: Account,
    val profile: Profile?,
    val marketingTracking: MarketingTracking,
    val referralCode: String?,
    val analyticsId: AnalyticsId? = null,
    val organisationAccountId: OrganisationAccountId?
) {
    val id get() = this.account.id

    fun hasProfile(): Boolean {
        return profile?.firstName?.isNotEmpty() ?: false
    }

    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    fun <T> runIfHasContactDetails(block: (contactDetails: ContactDetails) -> T): T? {
        val email = this.account.email ?: return null
        return this.profile?.let {
            block(
                ContactDetails(
                    firstName = this.profile.firstName,
                    lastName = this.profile.lastName,
                    hasOptedIntoMarketing = this.profile.hasOptedIntoMarketing,
                    email = email
                )
            )
        }
    }

    override fun toString(): String {
        return "User(id=$id)"
    }

    data class ContactDetails(
        val firstName: String,
        val lastName: String,
        val hasOptedIntoMarketing: Boolean = false,
        val email: String
    )
}
