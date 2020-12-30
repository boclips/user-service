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
    val features: Map<FeatureKey, Boolean>?,
    val prices: Map<VideoTypeKey, BigDecimal?>?,
)

enum class VideoTypeKey {
    INSTRUCTIONAL,
    NEWS,
    STOCK
}

enum class FeatureKey {
    LTI_COPY_RESOURCE_LINK,
    LTI_SLS_TERMS_BUTTON,
    TEACHERS_HOME_BANNER,
    TEACHERS_HOME_SUGGESTED_VIDEOS,
    TEACHERS_HOME_PROMOTED_COLLECTIONS,
    TEACHERS_SUBJECTS,
    USER_DATA_HIDDEN
}

data class LocationDocument(
    val code: String
)
