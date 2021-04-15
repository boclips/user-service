package com.boclips.users.presentation.converters

import com.boclips.users.api.response.feature.FeatureKeyResource
import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.domain.model.feature.Feature

class FeatureConverter {
    companion object {
        fun toFeatureResource(features: Map<Feature, Boolean>): FeaturesResource {
            return features.mapKeys { pair ->
                when (pair.key) {
                    Feature.LTI_COPY_RESOURCE_LINK -> FeatureKeyResource.LTI_COPY_RESOURCE_LINK
                    Feature.LTI_SLS_TERMS_BUTTON -> FeatureKeyResource.LTI_SLS_TERMS_BUTTON
                    Feature.LTI_RESPONSIVE_VIDEO_CARD -> FeatureKeyResource.LTI_RESPONSIVE_VIDEO_CARD
                    Feature.TEACHERS_HOME_BANNER -> FeatureKeyResource.TEACHERS_HOME_BANNER
                    Feature.TEACHERS_HOME_SUGGESTED_VIDEOS -> FeatureKeyResource.TEACHERS_HOME_SUGGESTED_VIDEOS
                    Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS -> FeatureKeyResource.TEACHERS_HOME_PROMOTED_COLLECTIONS
                    Feature.TEACHERS_SUBJECTS -> FeatureKeyResource.TEACHERS_SUBJECTS
                    Feature.USER_DATA_HIDDEN -> FeatureKeyResource.USER_DATA_HIDDEN
                    Feature.BO_WEB_APP_COPY_OLD_LINK_BUTTON -> FeatureKeyResource.BO_WEB_APP_COPY_OLD_LINK_BUTTON
                    Feature.BO_WEB_APP_ADDITIONAL_SERVICES -> FeatureKeyResource.BO_WEB_APP_ADDITIONAL_SERVICES
                    Feature.BO_WEB_APP_HIDE_PRICES -> FeatureKeyResource.BO_WEB_APP_HIDE_PRICES
                }
            }
        }
    }
}
