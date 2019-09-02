package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

sealed class UserUpdateCommand {
    data class ReplaceFirstName(val firstName: String) : UserUpdateCommand()
    data class ReplaceLastName(val lastName: String) : UserUpdateCommand()
    data class ReplaceSubjects(val subjects: List<Subject>) : UserUpdateCommand()
    data class ReplaceAges(val ages: List<Int>) : UserUpdateCommand()
    data class ReplaceHasOptedIntoMarketing(val hasOptedIntoMarketing: Boolean) : UserUpdateCommand()
    data class ReplaceReferralCode(val referralCode: String) : UserUpdateCommand()
    data class ReplaceCountry(val country: Country) : UserUpdateCommand()
    data class ReplaceState(val state: State) : UserUpdateCommand()
    data class ReplaceSchool(val school: String) : UserUpdateCommand()
    data class ReplaceOrganisationId(val organisationId: OrganisationId) : UserUpdateCommand()
    data class ReplaceMarketingTracking(
        val utmCampaign: String?,
        val utmSource: String?,
        val utmMedium: String?,
        val utmContent: String?,
        val utmTerm: String?
    ) : UserUpdateCommand()
}
