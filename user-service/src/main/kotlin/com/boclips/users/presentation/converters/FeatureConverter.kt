package com.boclips.users.presentation.converters

import com.boclips.users.api.response.feature.FeatureKeyResource
import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.domain.model.feature.Feature

class FeatureConverter {
    companion object {
        fun toFeatureResource(features: Map<Feature, Boolean>): FeaturesResource {
            return features.mapKeys { pair ->
                when (pair.key) {
                    Feature.LTI_SLS_TERMS_BUTTON -> FeatureKeyResource.LTI_SLS_TERMS_BUTTON.toString()
                    Feature.LTI_RESPONSIVE_VIDEO_CARD -> FeatureKeyResource.LTI_RESPONSIVE_VIDEO_CARD.toString()
                    Feature.LTI_AGE_FILTER -> FeatureKeyResource.LTI_AGE_FILTER.toString()
                    Feature.USER_DATA_HIDDEN -> FeatureKeyResource.USER_DATA_HIDDEN.toString()
                    Feature.BO_WEB_APP_COPY_OLD_LINK_BUTTON -> FeatureKeyResource.BO_WEB_APP_COPY_OLD_LINK_BUTTON.toString()
                    Feature.BO_WEB_APP_ADDITIONAL_SERVICES -> FeatureKeyResource.BO_WEB_APP_ADDITIONAL_SERVICES.toString()
                    Feature.BO_WEB_APP_PRICES -> FeatureKeyResource.BO_WEB_APP_PRICES.toString()
                }
            }
        }
    }
}
