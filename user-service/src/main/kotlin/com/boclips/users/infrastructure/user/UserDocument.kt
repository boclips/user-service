package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationId
import java.time.Instant

data class UserDocument(
    val _id: String,
    var firstName: String?,
    var lastName: String?,
    val email: String?,
    val username: String?,
    var subjectIds: List<String>?,
    var ageRange: List<Int>?,
    val analyticsId: String?,
    var referralCode: String?,
    var shareCode: String?,
    var hasOptedIntoMarketing: Boolean?,
    var marketing: MarketingTrackingDocument?,
    var organisationId: String?,
    var accessExpiresOn: Instant?,
    var createdAt: Instant,
    var hasLifetimeAccess: Boolean,
    var role: String?
) {

    companion object {
        fun from(user: User): UserDocument {
            return UserDocument(
                _id = user.id.value,
                subjectIds = user.profile?.subjects?.map { it.id.value },
                ageRange = user.profile?.ages,
                analyticsId = user.analyticsId?.value,
                referralCode = user.referralCode,
                shareCode = user.teacherPlatformAttributes?.shareCode,
                firstName = user.profile?.firstName,
                lastName = user.profile?.lastName,
                email = user.identity.email,
                username = user.identity.username,
                hasOptedIntoMarketing = user.profile?.hasOptedIntoMarketing ?: false,
                marketing = MarketingTrackingDocument(
                    utmCampaign = user.marketingTracking.utmCampaign,
                    utmSource = user.marketingTracking.utmSource,
                    utmMedium = user.marketingTracking.utmMedium,
                    utmTerm = user.marketingTracking.utmTerm,
                    utmContent = user.marketingTracking.utmContent
                ),
                organisationId = user.organisationId?.value,
                accessExpiresOn = user.accessExpiresOn?.toInstant(),
                createdAt = user.identity.createdAt.toInstant(),
                hasLifetimeAccess = user.teacherPlatformAttributes?.hasLifetimeAccess ?: false,
                role = user.profile?.role
            )
        }

        fun from(identity: Identity, organisationId: OrganisationId?): UserDocument {
            return UserDocument(
                _id = identity.id.value,
                subjectIds = null,
                ageRange = null,
                analyticsId = null,
                referralCode = null,
                shareCode = null,
                firstName = null,
                lastName = null,
                email = identity.email,
                username = identity.username,
                hasOptedIntoMarketing = false,
                marketing = null,
                organisationId = organisationId?.value,
                accessExpiresOn = null,
                createdAt = identity.createdAt.toInstant(),
                hasLifetimeAccess = false,
                role = null
            )
        }
    }
}
