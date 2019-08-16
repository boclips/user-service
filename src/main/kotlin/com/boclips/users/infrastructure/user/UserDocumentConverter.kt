package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.service.SubjectService

data class UserDocumentConverter(private val subjectService: SubjectService) {
    fun convertToUser(userDocument: UserDocument): User {
        return User(
            id = UserId(value = userDocument.id),
            activated = userDocument.activated,
            analyticsId = userDocument.analyticsId?.let { AnalyticsId(value = it) },
            subjects = userDocument.subjectIds.orEmpty().map { SubjectId(value = it) }.takeIf { it.isNotEmpty() }?.let { subjectService.getSubjectsById(it) } ?: emptyList(),
            ages = userDocument.ageRange.orEmpty(),
            referralCode = userDocument.referralCode?.let { it },
            firstName = userDocument.firstName.orEmpty(),
            lastName = userDocument.lastName.orEmpty(),
            email = userDocument.email.orEmpty(),
            hasOptedIntoMarketing = userDocument.hasOptedIntoMarketing ?: true,
            marketingTracking = MarketingTracking(
                utmSource = userDocument.marketing?.utmSource ?: "",
                utmContent = userDocument.marketing?.utmContent ?: "",
                utmMedium = userDocument.marketing?.utmMedium ?: "",
                utmTerm = userDocument.marketing?.utmTerm ?: "",
                utmCampaign = userDocument.marketing?.utmCampaign ?: ""
            ),
            associatedTo = userDocument.organisationId
                ?.let { UserSource.ApiClient(com.boclips.users.domain.model.organisation.OrganisationId(value = userDocument.organisationId)) }
                ?: UserSource.Boclips
        )
    }
}
