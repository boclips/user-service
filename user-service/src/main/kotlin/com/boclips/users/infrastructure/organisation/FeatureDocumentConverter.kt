package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.feature.Feature
import mu.KLogging

object FeatureDocumentConverter : KLogging() {
    fun fromDocument(featureDocument: FeatureDocument): Feature =
        when (featureDocument) {
            FeatureDocument.LTI_COPY_RESOURCE_LINK -> Feature.LTI_COPY_RESOURCE_LINK
            FeatureDocument.LTI_SLS_TERMS_BUTTON -> Feature.LTI_SLS_TERMS_BUTTON
            FeatureDocument.TEACHERS_HOME_BANNER -> Feature.TEACHERS_HOME_BANNER
            FeatureDocument.TEACHERS_HOME_SUGGESTED_VIDEOS -> Feature.TEACHERS_HOME_SUGGESTED_VIDEOS
            FeatureDocument.TEACHERS_HOME_PROMOTED_COLLECTIONS -> Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS
            FeatureDocument.TEACHERS_SUBJECTS -> Feature.TEACHERS_SUBJECTS
            FeatureDocument.USER_DATA_HIDDEN -> Feature.USER_DATA_HIDDEN
        }

    fun toDocument(feature: Feature): FeatureDocument =
        when (feature) {
            Feature.LTI_COPY_RESOURCE_LINK -> FeatureDocument.LTI_COPY_RESOURCE_LINK
            Feature.LTI_SLS_TERMS_BUTTON -> FeatureDocument.LTI_SLS_TERMS_BUTTON
            Feature.TEACHERS_HOME_BANNER -> FeatureDocument.TEACHERS_HOME_BANNER
            Feature.TEACHERS_HOME_SUGGESTED_VIDEOS -> FeatureDocument.TEACHERS_HOME_SUGGESTED_VIDEOS
            Feature.TEACHERS_HOME_PROMOTED_COLLECTIONS -> FeatureDocument.TEACHERS_HOME_PROMOTED_COLLECTIONS
            Feature.TEACHERS_SUBJECTS -> FeatureDocument.TEACHERS_SUBJECTS
            Feature.USER_DATA_HIDDEN -> FeatureDocument.USER_DATA_HIDDEN
        }
}