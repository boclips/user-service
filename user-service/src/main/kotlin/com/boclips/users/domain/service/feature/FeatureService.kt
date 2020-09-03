package com.boclips.users.domain.service.feature

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import org.springframework.stereotype.Service

@Service
class FeatureService(
    private val userRepository: UserRepository,
    private val organisationRepository: OrganisationRepository
) {

    fun getFeatures(userId: UserId): Map<Feature, Boolean> {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)

        val organisation = user.organisation?.id?.let { organisationRepository.findOrganisationById(it) }

        return Feature.values().map {
            it to (organisation?.features?.get(it) ?: it.defaultValue)
        }.toMap()
    }
}
