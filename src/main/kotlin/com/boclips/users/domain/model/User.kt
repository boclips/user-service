package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import java.time.ZonedDateTime

data class User(
    val identity: Identity,
    val profile: Profile?,
    val teacherPlatformAttributes: TeacherPlatformAttributes?,
    val marketingTracking: MarketingTracking,
    val referralCode: String?,
    val analyticsId: AnalyticsId? = null,
    val organisationId: OrganisationId?,
    val accessExpiresOn: ZonedDateTime?
) {
    val id get() = this.identity.id

    fun hasOnboarded(): Boolean {
        return isActivated()
    }

    fun isActivated() : Boolean {
        return profile?.firstName?.isNotEmpty() ?: false
    }

    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    fun getContactDetails(): ContactDetails? {
        val email = this.identity.email ?: return null
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
