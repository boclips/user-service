package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry

interface AmericanSchoolsProvider {
    fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry>
}