package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_API
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_DISTRICT
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument.Companion.TYPE_NO_ORGANISATION

data class UserDocumentConverter(private val subjectService: SubjectService) {
    fun convertToUser(userDocument: UserDocument): User {
        return User(
            account = Account(
                id = UserId(value = userDocument.id),
                username = userDocument.username ?: userDocument.email.orEmpty()
            ),
            profile = Profile(
                firstName = userDocument.firstName.orEmpty(),
                lastName = userDocument.lastName.orEmpty(),
                hasOptedIntoMarketing = userDocument.hasOptedIntoMarketing ?: false,
                subjects = userDocument.subjectIds.orEmpty().map { SubjectId(value = it) }.takeIf { it.isNotEmpty() }?.let {
                    subjectService.getSubjectsById(
                        it
                    )
                } ?: emptyList(),
                ages = userDocument.ageRange.orEmpty(),
                country = userDocument.country.orEmpty(),
                state = userDocument.state.orEmpty(),
                school = userDocument.school.orEmpty()

            ),
            analyticsId = userDocument.analyticsId?.let { AnalyticsId(value = it) },
            referralCode = userDocument.referralCode?.let { it },
            marketingTracking = MarketingTracking(
                utmSource = userDocument.marketing?.utmSource ?: "",
                utmContent = userDocument.marketing?.utmContent ?: "",
                utmMedium = userDocument.marketing?.utmMedium ?: "",
                utmTerm = userDocument.marketing?.utmTerm ?: "",
                utmCampaign = userDocument.marketing?.utmCampaign ?: ""
            ),
            organisationId = userDocument.organisationId?.let { OrganisationId(it) }
        )
    }
}
