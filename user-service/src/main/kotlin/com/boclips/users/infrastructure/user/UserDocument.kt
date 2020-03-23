package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
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
    var hasLifetimeAccess: Boolean
) {

    companion object {
        fun from(user: User): UserDocument {
            return UserDocument(
                id = user.id.value,
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
                hasLifetimeAccess = user.teacherPlatformAttributes?.hasLifetimeAccess ?: false
            )
        }

        fun from(identity: Identity, organisationId: OrganisationId?): UserDocument {
            return UserDocument(
                id = identity.id.value,
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
                hasLifetimeAccess = false
            )
        }
    }
}
