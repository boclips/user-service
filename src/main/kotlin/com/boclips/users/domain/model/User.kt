package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import java.time.ZonedDateTime

/**
 * TODO(AO/EV): Remove this field once the lifetime flag is set on the user below.
 */
const val PLATFORM_CLOSURE_DATE = "2019-12-05T00:00:00Z"

data class User(
    val account: Account,
    val profile: Profile?,
    val marketingTracking: MarketingTracking,
    val referralCode: String?,
    val analyticsId: AnalyticsId? = null,
    val organisationAccountId: OrganisationAccountId?,
    val accessExpiresOn: ZonedDateTime?,
    val hasLifetimeAccess: Boolean? = false
) {
    val id get() = this.account.id

    fun hasOnboarded(): Boolean {
        return organisationAccountId?.value?.isNotEmpty() ?: false
    }

    fun wasCreatedBeforePlatformClosure(): Boolean {
        return account.createdAt == null || account.createdAt < ZonedDateTime.parse(PLATFORM_CLOSURE_DATE)
    }

    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    fun getContactDetails(): ContactDetails? {
        val email = this.account.email ?: return null
        return this.profile?.let {
            ContactDetails(
                firstName = this.profile.firstName,
                lastName = this.profile.lastName,
                hasOptedIntoMarketing = this.profile.hasOptedIntoMarketing,
                email = email
            )
        }
    }

    data class ContactDetails(
        val firstName: String,
        val lastName: String,
        val hasOptedIntoMarketing: Boolean = false,
        val email: String
    )
}
