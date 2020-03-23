package com.boclips.users.domain.model

data class Subject(val id: SubjectId, val name: String)

data class SubjectId(val value: String)
