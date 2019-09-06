package com.boclips.users.infrastructure.schooldigger

data class SchoolDiggerMatchesResponse(val schoolMatches: List<SchoolDiggerSchool> = emptyList())

data class SchoolDiggerSchool(
    val schoolid: String,
    val schoolName: String
)