package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.Organisation
import java.time.ZonedDateTime

class User(
    val identity: Identity,
    val profile: Profile?,
    val teacherPlatformAttributes: TeacherPlatformAttributes?,
    val marketingTracking: MarketingTracking,
    val referralCode: String?,
    val analyticsId: AnalyticsId? = null,
    val organisation: Organisation? = null,
    val shareCode: String? = null,
    accessExpiresOn: ZonedDateTime?
) {
    val id get() = this.identity.id

    val accessExpiresOn: ZonedDateTime? = accessExpiresOn
        get() {
            val hasLifetimeAccess = field == null
            return if (hasLifetimeAccess) field else { organisation?.accessExpiryDate ?: field }
        }

    val features get() = organisation?.features ?: Feature.DEFAULT_VALUES

    fun hasOnboarded(): Boolean {
        return profile?.firstName?.isNotEmpty() ?: false
    }

    fun hasDetailsHidden(): Boolean {
        return features.getOrDefault(Feature.USER_DATA_HIDDEN, Feature.USER_DATA_HIDDEN.defaultValue)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (identity != other.identity) return false
        if (profile != other.profile) return false
        if (teacherPlatformAttributes != other.teacherPlatformAttributes) return false
        if (marketingTracking != other.marketingTracking) return false
        if (referralCode != other.referralCode) return false
        if (analyticsId != other.analyticsId) return false
        if (organisation != other.organisation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = identity.hashCode()
        result = 31 * result + (profile?.hashCode() ?: 0)
        result = 31 * result + (teacherPlatformAttributes?.hashCode() ?: 0)
        result = 31 * result + marketingTracking.hashCode()
        result = 31 * result + (referralCode?.hashCode() ?: 0)
        result = 31 * result + (analyticsId?.hashCode() ?: 0)
        result = 31 * result + (organisation?.hashCode() ?: 0)
        return result
    }

    data class ContactDetails(
        val firstName: String,
        val lastName: String,
        val hasOptedIntoMarketing: Boolean = false,
        val email: String
    )
}
