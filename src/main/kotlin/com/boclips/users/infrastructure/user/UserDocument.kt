package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.Platform
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

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
    var hasOptedIntoMarketing: Boolean?,
    var marketing: MarketingTrackingDocument?,
    val organisationId: String?,
    val country: String?
) {
    companion object {
        fun from(user: User): UserDocument {
            return UserDocument(
                id = user.id.value,
                subjectIds = user.profile?.subjects?.map { it.id.value },
                ageRange = user.profile?.ages,
                analyticsId = user.analyticsId?.value,
                referralCode = user.referralCode,
                firstName = user.profile?.firstName,
                lastName = user.profile?.lastName,
                email = user.account.email,
                username = user.account.username,
                hasOptedIntoMarketing = user.profile?.hasOptedIntoMarketing ?: false,
                marketing = MarketingTrackingDocument(
                    utmCampaign = user.marketingTracking.utmCampaign,
                    utmSource = user.marketingTracking.utmSource,
                    utmMedium = user.marketingTracking.utmMedium,
                    utmTerm = user.marketingTracking.utmTerm,
                    utmContent = user.marketingTracking.utmContent
                ),
                organisationId = when (user.account.platform) {
                    is Platform.BoclipsForTeachers -> null
                    is Platform.ApiCustomer -> user.account.platform.organisationId.value
                },
                country = user.profile?.country
            )
        }
        fun from(account: Account): UserDocument {
            return UserDocument(
                id = account.id.value,
                subjectIds = null,
                ageRange = null,
                analyticsId = null,
                referralCode = null,
                firstName = null,
                lastName = null,
                email = account.email,
                username = account.username,
                hasOptedIntoMarketing = false,
                marketing = null,
                organisationId = when (account.platform) {
                    is Platform.BoclipsForTeachers -> null
                    is Platform.ApiCustomer -> account.platform.organisationId.value
                },
                country = null
            )
        }
    }
}
