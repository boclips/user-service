package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationType
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

data class OrganisationDocument(
    val _id: ObjectId?,
    val name: String,
    val role: String?,
    val domain: String?,
    val externalId: String?,
    val deploymentId: String?,
    val type: OrganisationType,
    val country: LocationDocument?,
    val state: LocationDocument?,
    val postcode: String?,
    val allowsOverridingUserIds: Boolean?,
    val parent: OrganisationDocument? = null,
    val accessExpiresOn: Instant? = null,
    val tags: Set<String>? = null,
    val billing: Boolean? = null,
    val contentPackageId: String? = null,
    val contentPackageByClient: Map<String, String>? = null,
    val features: Map<FeatureKey, Boolean>?,
    val prices: CustomPricesDocument?,
    val legacyId: String? = null,
    val logoUrl: String? = null
)

enum class VideoTypeKey {
    INSTRUCTIONAL,
    NEWS,
    STOCK
}

data class PriceDocument(
    val amount: BigDecimal,
    val currency: String
)

data class CustomPricesDocument(
    val videoTypePrices: Map<VideoTypeKey, PriceDocument>?,
    val channelPrices: Map<String, PriceDocument>?,
)

enum class FeatureKey {
    LTI_COPY_RESOURCE_LINK,
    LTI_SLS_TERMS_BUTTON,
    LTI_RESPONSIVE_VIDEO_CARD,
    TEACHERS_HOME_BANNER,
    TEACHERS_HOME_SUGGESTED_VIDEOS,
    TEACHERS_HOME_PROMOTED_COLLECTIONS,
    TEACHERS_SUBJECTS,
    USER_DATA_HIDDEN,
    BO_WEB_APP_COPY_OLD_LINK_BUTTON,
    BO_WEB_APP_ADDITIONAL_SERVICES
}

data class LocationDocument(
    val code: String
)
