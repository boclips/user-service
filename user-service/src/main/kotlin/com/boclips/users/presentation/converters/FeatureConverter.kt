package com.boclips.users.presentation.converters

import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.api.response.feature.FeaturesEmbeddedResource
import com.boclips.users.api.response.feature.FeaturesWrapper
import com.boclips.users.domain.model.feature.Feature
import org.springframework.stereotype.Component

@Component
class FeatureConverter {

    fun toFeaturesResource(features: Map<Feature, Boolean>): FeaturesEmbeddedResource {
        return FeaturesEmbeddedResource(_embedded = FeaturesWrapper(
            features = toFeatureResource(features)
        ))
    }

    fun toFeatureResource(features: Map<Feature, Boolean>): FeaturesResource {
        return features.mapKeys { pair -> pair.key.toString() }
    }
}
