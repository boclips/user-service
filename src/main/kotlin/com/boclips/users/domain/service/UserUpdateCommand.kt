package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject

sealed class UserUpdateCommand {
    data class ReplaceFirstName(val firstName: String) : UserUpdateCommand()
    data class ReplaceLastName(val lastName: String) : UserUpdateCommand()
    data class ReplaceSubjects(val subjects: List<Subject>) : UserUpdateCommand()
    data class ReplaceAges(val ages: List<Int>) : UserUpdateCommand()
    data class ReplaceHasOptedIntoMarketing(val hasOptedIntoMarketing: Boolean) : UserUpdateCommand()
    data class ReplaceReferralCode(val referralCode: String) : UserUpdateCommand()
    data class ReplaceCountry(val country: String) : UserUpdateCommand()
    data class ReplaceState(val state: String) : UserUpdateCommand()
    data class ReplaceSchool(val school: String) : UserUpdateCommand()
    data class ReplaceMarketingTracking(
        val utmCampaign: String?,
        val utmSource: String?,
        val utmMedium: String?,
        val utmContent: String?,
        val utmTerm: String?
    ) : UserUpdateCommand()
}
