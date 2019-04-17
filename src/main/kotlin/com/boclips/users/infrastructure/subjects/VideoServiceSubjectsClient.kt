package com.boclips.users.infrastructure.subjects

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.service.SubjectService
import com.boclips.videos.service.client.VideoServiceClient

data class VideoServiceSubjectsClient(val videoServiceClient: VideoServiceClient) : SubjectService {
    override fun getSubjectsById(subjectIds: List<SubjectId>): List<Subject> {
        val subjects = videoServiceClient.subjects

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