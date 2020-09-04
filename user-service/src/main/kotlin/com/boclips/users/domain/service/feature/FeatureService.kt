package com.boclips.users.domain.service.feature

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import org.springframework.stereotype.Service

@Service
class FeatureService(
    private val userRepository: UserRepository
) {
    fun getFeatures(userId: UserId): Map<Feature, Boolean> {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)

        return Feature.values().map {
            it to (user.organisation?.features?.get(it) ?: it.defaultValue)
        }.toMap()
    }
}
