package com.boclips.users.domain.model.feature

enum class Feature(val defaultValue: Boolean) {
    LTI_COPY_RESOURCE_LINK(false),
    LTI_SLS_TERMS_BUTTON(false),
    TEACHERS_HOME_BANNER(true),
    TEACHERS_HOME_SUGGESTED_VIDEOS(true),
    TEACHERS_HOME_PROMOTED_COLLECTIONS(true),
    TEACHERS_SUBJECTS(true),
    USER_DATA_HIDDEN(false);

    companion object {

        val DEFAULT_VALUES: Map<Feature, Boolean> = values().map { it to it.defaultValue }.toMap()

        fun withAllFeatures(features: Map<Feature, Boolean>?): Map<Feature, Boolean> = values().map {
            it to (features?.get(it) ?: it.defaultValue)
        }.toMap()
    }
}
