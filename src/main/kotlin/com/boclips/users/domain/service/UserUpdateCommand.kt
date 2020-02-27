package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.account.OrganisationId
import java.time.ZonedDateTime

sealed class UserUpdateCommand {
    data class ReplaceFirstName(val firstName: String) : UserUpdateCommand()
    data class ReplaceLastName(val lastName: String) : UserUpdateCommand()
    data class ReplaceSubjects(val subjects: List<Subject>) : UserUpdateCommand()
    data class ReplaceAges(val ages: List<Int>) : UserUpdateCommand()
    data class ReplaceHasOptedIntoMarketing(val hasOptedIntoMarketing: Boolean) : UserUpdateCommand()
    data class ReplaceReferralCode(val referralCode: String) : UserUpdateCommand()
    data class ReplaceOrganisationId(val organisationId: OrganisationId) : UserUpdateCommand()
    data class ReplaceMarketingTracking(
        val utmCampaign: String?,
        val utmSource: String?,
        val utmMedium: String?,
        val utmContent: String?,
        val utmTerm: String?
    ) : UserUpdateCommand()
    data class ReplaceAccessExpiresOn(val accessExpiresOn: ZonedDateTime) : UserUpdateCommand()
    data class ReplaceHasLifetimeAccess(val hasLifetimeAccess: Boolean) : UserUpdateCommand()
    data class ReplaceShareCode(val shareCode: String) : UserUpdateCommand()
}
