package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter
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
    var organisation: OrganisationDocument?,
    var accessExpiresOn: Instant?,
    var createdAt: Instant,
    var hasLifetimeAccess: Boolean,
    var role: String?,
    var profileSchool: OrganisationDocument?
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
                organisation = user.organisation?.let(OrganisationDocumentConverter::toDocument),
                accessExpiresOn = user.accessExpiresOn?.toInstant(),
                createdAt = user.identity.createdAt.toInstant(),
                hasLifetimeAccess = user.teacherPlatformAttributes?.hasLifetimeAccess ?: false,
                role = user.profile?.role,
                profileSchool = user.profile?.school?.let(OrganisationDocumentConverter::toDocument)
            )
        }
    }
}
