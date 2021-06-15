package com.boclips.users.domain.model.feature

enum class Feature(val defaultValue: Boolean) {
    LTI_SLS_TERMS_BUTTON(false),
    LTI_RESPONSIVE_VIDEO_CARD(false),
    LTI_AGE_FILTER(true),
    USER_DATA_HIDDEN(false),
    BO_WEB_APP_COPY_OLD_LINK_BUTTON(false),
    BO_WEB_APP_ADDITIONAL_SERVICES(true),
    BO_WEB_APP_PRICES(true);

    companion object {

        val DEFAULT_VALUES: Map<Feature, Boolean> = values().map { it to it.defaultValue }.toMap()

        fun withAllFeatures(features: Map<Feature, Boolean>?): Map<Feature, Boolean> = values().map {
            it to (features?.get(it) ?: it.defaultValue)
        }.toMap()

        fun isValid(value: String): Boolean {
            return values().any { it.name == value }
        }
    }
}
