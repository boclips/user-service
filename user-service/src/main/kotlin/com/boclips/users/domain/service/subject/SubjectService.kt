package com.boclips.users.domain.service.subject

import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId

interface SubjectService {
    fun getSubjectsById(subjectIds: List<SubjectId>): List<Subject>
    fun allSubjectsExist(subjectIds: List<SubjectId>): Boolean
}
