package com.boclips.users.infrastructure.subjects

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.service.SubjectService

data class VideoServiceSubjectsClient(val cacheableSubjectsClient: CacheableSubjectsClient) : SubjectService {
    override fun allSubjectsExist(subjectIds: List<SubjectId>): Boolean {
        val knownSubjectsIds = cacheableSubjectsClient.getSubjects().map { it.id }
        return knownSubjectsIds.containsAll(subjectIds.map { it.value })
    }

    override fun getSubjectsById(subjectIds: List<SubjectId>): List<Subject> {
        val subjects = cacheableSubjectsClient.getSubjects()

        return subjectIds.mapNotNull { subjectId ->
            subjects.find { it.id == subjectId.value }?.let {
                Subject(
                    id = SubjectId(value = it.id),
                    name = it.name
                )
            }
        }
    }
}