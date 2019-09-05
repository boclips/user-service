package com.boclips.users.infrastructure.schooldigger

data class SchoolDiggerMatchesResponse(val schools: List<SchoolResponse> = emptyList())

data class SchoolResponse(val schoolid: String, val schoolName: String, val city: String, val state: String)