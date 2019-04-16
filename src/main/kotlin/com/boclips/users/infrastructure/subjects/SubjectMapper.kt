package com.boclips.users.infrastructure.subjects

import com.boclips.videos.service.client.VideoServiceClient

data class SubjectMapper(val videoServiceClient: VideoServiceClient) {
    fun getName(subjectId: String): String? {
        return videoServiceClient.subjects.find { it.id == subjectId }?.name
    }

    fun getNames(subjectIds: List<String>): List<String> {
        return subjectIds.mapNotNull { getName(it) }
    }
}