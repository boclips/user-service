package com.boclips.users.infrastructure

import com.boclips.videos.service.client.VideoServiceClient

class SubjectValidator(private val videoServiceClient: VideoServiceClient) {
    fun isValid(subjects: List<String>): Boolean {
        val knownSubjectsIds = videoServiceClient.subjects.map { it.id }
        return knownSubjectsIds.containsAll(subjects)
    }
}