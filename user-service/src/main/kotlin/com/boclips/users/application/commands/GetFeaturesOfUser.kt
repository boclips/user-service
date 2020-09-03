package com.boclips.users.application.commands

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.service.feature.FeatureService
import org.springframework.stereotype.Component

@Component
class GetFeaturesOfUser(private val featureService: FeatureService) {
    operator fun invoke(userId: UserId): Map<Feature, Boolean> {
        return featureService.getFeatures(userId)
    }
}