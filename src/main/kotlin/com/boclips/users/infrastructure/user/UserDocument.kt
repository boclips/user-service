package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.OrganisationType
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
    val organisationId: String?,
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
                organisationId = extractOrganisationId(user.account.organisationType),
                organisationType = OrganisationTypeDocument(
                    id = extractOrganisationId(user.account.organisationType),
                    type = user.account.organisationType
                ),
                country = user.profile?.country,
                state = user.profile?.state,
                school = user.profile?.school
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
                organisationId = extractOrganisationId(account.organisationType),
                organisationType = OrganisationTypeDocument(
                    id = extractOrganisationId(account.organisationType),
                    type = account.organisationType
                ),
                country = null,
                state = null,
                school = null
            )
        }

        private fun extractOrganisationId(organisationType: OrganisationType) : String? {
            return when (organisationType) {
                is OrganisationType.BoclipsForTeachers -> null
                is OrganisationType.ApiCustomer -> organisationType.organisationId.value
                is OrganisationType.District -> organisationType.organisationId.value
            }
        }
    }
}
