package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.feature.Feature
import mu.KLogging

object FeatureDocumentConverter : KLogging() {
    fun fromDocument(featureKey: FeatureKey): Feature =
        when (featureKey) {
            FeatureKey.LTI_SLS_TERMS_BUTTON -> Feature.LTI_SLS_TERMS_BUTTON
            FeatureKey.LTI_AGE_FILTER -> Feature.LTI_AGE_FILTER
            FeatureKey.TEACHERS_HOME_BANNER -> Feature.TEACHERS_HOME_BANNER
            FeatureKey.TEACHERS_HOME_SUGGESTED_VIDEOS -> Feature.TEACHERS_HOME_SUGGESTED_VIDEOS
            FeatureKey.TEACHERS_HOME_PROMOTED_COLLECTIONS -> Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS
            FeatureKey.TEACHERS_SUBJECTS -> Feature.TEACHERS_SUBJECTS
            FeatureKey.USER_DATA_HIDDEN -> Feature.USER_DATA_HIDDEN
            FeatureKey.LTI_RESPONSIVE_VIDEO_CARD -> Feature.LTI_RESPONSIVE_VIDEO_CARD
            FeatureKey.BO_WEB_APP_COPY_OLD_LINK_BUTTON -> Feature.BO_WEB_APP_COPY_OLD_LINK_BUTTON
            FeatureKey.BO_WEB_APP_ADDITIONAL_SERVICES -> Feature.BO_WEB_APP_ADDITIONAL_SERVICES
            FeatureKey.BO_WEB_APP_PRICES -> Feature.BO_WEB_APP_PRICES
        }

    fun toDocument(feature: Feature): FeatureKey =
        when (feature) {
            Feature.LTI_SLS_TERMS_BUTTON -> FeatureKey.LTI_SLS_TERMS_BUTTON
            Feature.LTI_AGE_FILTER -> FeatureKey.LTI_AGE_FILTER
            Feature.TEACHERS_HOME_BANNER -> FeatureKey.TEACHERS_HOME_BANNER
            Feature.TEACHERS_HOME_SUGGESTED_VIDEOS -> FeatureKey.TEACHERS_HOME_SUGGESTED_VIDEOS
            Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS -> FeatureKey.TEACHERS_HOME_PROMOTED_COLLECTIONS
            Feature.TEACHERS_SUBJECTS -> FeatureKey.TEACHERS_SUBJECTS
            Feature.USER_DATA_HIDDEN -> FeatureKey.USER_DATA_HIDDEN
            Feature.LTI_RESPONSIVE_VIDEO_CARD -> FeatureKey.LTI_RESPONSIVE_VIDEO_CARD
            Feature.BO_WEB_APP_COPY_OLD_LINK_BUTTON -> FeatureKey.BO_WEB_APP_COPY_OLD_LINK_BUTTON
            Feature.BO_WEB_APP_ADDITIONAL_SERVICES -> FeatureKey.BO_WEB_APP_ADDITIONAL_SERVICES
            Feature.BO_WEB_APP_PRICES -> FeatureKey.BO_WEB_APP_PRICES
        }
}
