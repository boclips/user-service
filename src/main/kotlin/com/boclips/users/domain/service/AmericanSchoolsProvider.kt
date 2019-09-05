package com.boclips.users.domain.service

import com.boclips.users.infrastructure.schooldigger.SchoolResponse

interface AmericanSchoolsProvider {
    fun getSchools(state: String, school: String): List<SchoolResponse>
}