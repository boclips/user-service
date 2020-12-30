package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.feature.Feature
import mu.KLogging

object FeatureDocumentConverter : KLogging() {
    fun fromDocument(featureKey: FeatureKey): Feature =
        when (featureKey) {
            FeatureKey.LTI_COPY_RESOURCE_LINK -> Feature.LTI_COPY_RESOURCE_LINK
            FeatureKey.LTI_SLS_TERMS_BUTTON -> Feature.LTI_SLS_TERMS_BUTTON
            FeatureKey.TEACHERS_HOME_BANNER -> Feature.TEACHERS_HOME_BANNER
            FeatureKey.TEACHERS_HOME_SUGGESTED_VIDEOS -> Feature.TEACHERS_HOME_SUGGESTED_VIDEOS
            FeatureKey.TEACHERS_HOME_PROMOTED_COLLECTIONS -> Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS
            FeatureKey.TEACHERS_SUBJECTS -> Feature.TEACHERS_SUBJECTS
            FeatureKey.USER_DATA_HIDDEN -> Feature.USER_DATA_HIDDEN
        }

    fun toDocument(feature: Feature): FeatureKey =
        when (feature) {
            Feature.LTI_COPY_RESOURCE_LINK -> FeatureKey.LTI_COPY_RESOURCE_LINK
            Feature.LTI_SLS_TERMS_BUTTON -> FeatureKey.LTI_SLS_TERMS_BUTTON
            Feature.TEACHERS_HOME_BANNER -> FeatureKey.TEACHERS_HOME_BANNER
            Feature.TEACHERS_HOME_SUGGESTED_VIDEOS -> FeatureKey.TEACHERS_HOME_SUGGESTED_VIDEOS
            Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS -> FeatureKey.TEACHERS_HOME_PROMOTED_COLLECTIONS
            Feature.TEACHERS_SUBJECTS -> FeatureKey.TEACHERS_SUBJECTS
            Feature.USER_DATA_HIDDEN -> FeatureKey.USER_DATA_HIDDEN
        }
}