package com.boclips.users.presentation.converters

import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.api.response.feature.FeaturesWrapper
import com.boclips.users.domain.model.feature.Feature
import org.springframework.stereotype.Component

@Component
class FeatureConverter {

    fun toResource(features: Map<Feature, Boolean>): FeaturesResource {
        return FeaturesResource(_embedded = FeaturesWrapper(
            features = features.mapKeys { pair -> pair.key.toString() })
        )
    }
}