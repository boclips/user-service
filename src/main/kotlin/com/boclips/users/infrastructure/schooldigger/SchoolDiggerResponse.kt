package com.boclips.users.infrastructure.schooldigger

data class SchoolDiggerMatchesResponse(val schoolMatches: List<SchoolDiggerSchool> = emptyList())

data class SchoolDiggerSchool(
    val schoolid: String,
    val schoolName: String
)

data class SchoolDiggerSchoolDetail(
    val schoolid: String,
    val schoolName: String,
    val address: SchoolDiggerAddress,
    val district: SchoolDiggerDistrict?
)

data class SchoolDiggerAddress(
    val state: String,
    val zip: String
)

data class SchoolDiggerDistrict(
    val districtID: String,
    val districtName: String
)
