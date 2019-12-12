package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.School

interface AmericanSchoolsProvider {
    fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry>
    fun fetchSchool(schoolId: String):  Pair<School, District?>?
}
