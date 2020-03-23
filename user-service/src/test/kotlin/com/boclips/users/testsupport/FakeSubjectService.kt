package com.boclips.users.testsupport

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.service.SubjectService

class FakeSubjectService : SubjectService {

    private val subjects: MutableList<Subject> = mutableListOf()

    fun addSubject(subject: Subject): Subject {
        subjects.add(subject)
        return subject
    }

    override fun getSubjectsById(subjectIds: List<SubjectId>): List<Subject> {
        return this.subjects.filter { subject -> subjectIds.contains(subject.id) }
    }

    override fun allSubjectsExist(subjectIds: List<SubjectId>): Boolean {
        return subjectIds.all { id -> subjects.find { subject -> subject.id == id } != null }
    }

    fun reset() {
        subjects.clear()
    }
}
