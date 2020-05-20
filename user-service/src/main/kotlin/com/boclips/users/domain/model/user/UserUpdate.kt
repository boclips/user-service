package com.boclips.users.domain.model.user

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.subject.Subject
import java.time.ZonedDateTime

sealed class UserUpdate {
    data class ReplaceFirstName(val firstName: String) : UserUpdate()
    data class ReplaceLastName(val lastName: String) : UserUpdate()
    data class ReplaceSubjects(val subjects: List<Subject>) : UserUpdate()
    data class ReplaceAges(val ages: List<Int>) : UserUpdate()
    data class ReplaceHasOptedIntoMarketing(val hasOptedIntoMarketing: Boolean) : UserUpdate()
    data class ReplaceReferralCode(val referralCode: String) : UserUpdate()
    data class ReplaceOrganisation(val organisation: Organisation) : UserUpdate()
    data class ReplaceProfileSchool(val school: School) : UserUpdate()
    data class ReplaceRole(val role: String) : UserUpdate()
    data class ReplaceMarketingTracking(
        val utmCampaign: String?,
        val utmSource: String?,
        val utmMedium: String?,
        val utmContent: String?,
        val utmTerm: String?
    ) : UserUpdate()

    data class ReplaceAccessExpiresOn(val accessExpiresOn: ZonedDateTime) : UserUpdate()
    data class ReplaceHasLifetimeAccess(val hasLifetimeAccess: Boolean) : UserUpdate()
    data class ReplaceShareCode(val shareCode: String) : UserUpdate()
}
