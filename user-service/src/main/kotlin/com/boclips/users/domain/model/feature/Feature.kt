package com.boclips.users.domain.model.feature

enum class Feature(val defaultValue: Boolean) {
    LTI_COPY_RESOURCE_LINK(false),
    TEACHERS_HOME_BANNER(true),
    TEACHERS_HOME_SUGGESTED_VIDEOS(true),
    TEACHERS_HOME_PROMOTED_COLLECTIONS(true),
    TEACHERS_SUBJECTS(true);

    companion object {
        fun withAllFeatures(features: Map<Feature, Boolean>?) = values().map {
            it to (features?.get(it) ?: it.defaultValue)
        }.toMap()
    }
}
