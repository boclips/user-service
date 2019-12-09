package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.service.SubjectService
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class UserDocumentConverter(
    private val subjectService: SubjectService
) {
    fun convertToUser(userDocument: UserDocument): User {
        return User(
            account = Account(
                id = UserId(value = userDocument.id),
                username = userDocument.username ?: userDocument.email.orEmpty(),
                createdAt = ZonedDateTime.ofInstant(userDocument.createdAt, ZoneOffset.UTC)
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
                ages = userDocument.ageRange.orEmpty()
            ),
            analyticsId = userDocument.analyticsId?.let { AnalyticsId(value = it) },
            referralCode = userDocument.referralCode?.let { it },
            shareCode = userDocument.shareCode?.let { it },
            marketingTracking = MarketingTracking(
                utmSource = userDocument.marketing?.utmSource ?: "",
                utmContent = userDocument.marketing?.utmContent ?: "",
                utmMedium = userDocument.marketing?.utmMedium ?: "",
                utmTerm = userDocument.marketing?.utmTerm ?: "",
                utmCampaign = userDocument.marketing?.utmCampaign ?: ""
            ),
            organisationAccountId = userDocument.organisationId?.let { OrganisationAccountId(it) },
            accessExpiresOn = userDocument.accessExpiresOn?.let { ZonedDateTime.ofInstant(it, ZoneOffset.UTC)},
            hasLifetimeAccess = userDocument.hasLifetimeAccess
        )
    }
}
