package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.User
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument
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
    var country: String?,
    var state: String?,
    var school: String?,
    var organisationType: OrganisationTypeDocument
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
                organisationType = OrganisationTypeConverter().toDocument(user.organisationType),
                country = user.profile?.country,
                state = user.profile?.state,
                school = user.profile?.school
            )
        }

        fun from(account: Account, organisationType: OrganisationType): UserDocument {
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
                organisationType = OrganisationTypeConverter().toDocument(organisationType),
                country = null,
                state = null,
                school = null
            )
        }
    }
}
