package com.boclips.users.api.factories

import com.boclips.users.api.request.user.CreateTeacherRequest

class CreateUserRequestFactory {
    companion object {
        fun sample(
            email: String? = "hans@muster.ch",
            password: String? = "heidiisgreat",
            referralCode: String? = "referralCode-123",
            recaptchaToken: String? = "03AOLTBLRK4xnVft-qESRgTGxK_4WAE...",
            analyticsId: String? = "mixpanel-123",
            utmCampaign: String? = null,
            utmContent: String? = null,
            utmMedium: String? = null,
            utmSource: String? = null,
            utmTerm: String? = null
        ): CreateTeacherRequest {
            return CreateTeacherRequest(
                email = email,
                password = password,
                analyticsId = analyticsId,
                referralCode = referralCode,
                recaptchaToken = recaptchaToken,
                utmCampaign = utmCampaign,
                utmContent = utmContent,
                utmMedium = utmMedium,
                utmSource = utmSource,
                utmTerm = utmTerm
            )
        }
    }
}