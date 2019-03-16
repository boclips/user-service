package com.boclips.users.domain.service

import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.model.referrals.ReferralId

interface ReferralProvider {
    fun createReferral(newReferral: NewReferral): ReferralId
}
