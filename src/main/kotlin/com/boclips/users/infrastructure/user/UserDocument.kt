package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val activated: Boolean,
    val subjectIds: List<String>?,
    val ageRange: List<Int>?,
    val analyticsId: String?,
    val referralCode: String?,
    val hasOptedIntoMarketing: Boolean?,
    val marketing: MarketingTrackingDocument?,
    val organisationId: String?
) {
    companion object {
        fun from(user: User): UserDocument {
            return UserDocument(
                id = user.id.value,
                activated = user.activated,
                subjectIds = user.subjects.map { it.id.value },
                ageRange = user.ageRange,
                analyticsId = user.analyticsId?.value,
                referralCode = user.referralCode,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                hasOptedIntoMarketing = user.hasOptedIntoMarketing,
                marketing = MarketingTrackingDocument(
                    utmCampaign = user.marketingTracking.utmCampaign,
                    utmSource = user.marketingTracking.utmSource,
                    utmMedium = user.marketingTracking.utmMedium,
                    utmTerm = user.marketingTracking.utmTerm,
                    utmContent = user.marketingTracking.utmContent
                ),
                organisationId = user.organisationId?.value
            )
        }
    }
}