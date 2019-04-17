package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId

interface SubjectService {
    fun getSubjectsById(subjectIds: List<SubjectId>): List<Subject>
}