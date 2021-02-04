package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.feature.Feature

import java.time.ZonedDateTime

enum class OrganisationType {
    API, SCHOOL, DISTRICT, LTI_DEPLOYMENT
}

sealed class Organisation(
    open val id: OrganisationId,
    open val name: String,
    open val address: Address,
    open val deal: Deal,
    open val role: String?,
    open val tags: Set<OrganisationTag>,
    open val domain: String?,
    features: Map<Feature, Boolean>?,
    open val legacyId: String?
) {

    val features: Map<Feature, Boolean>? = features
        get() = Feature.withAllFeatures(field)

    abstract fun type(): OrganisationType
    abstract val accessExpiryDate: ZonedDateTime?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Organisation

        if (id != other.id) return false
        if (name != other.name) return false
        if (address != other.address) return false
        if (deal != other.deal) return false
        if (role != other.role) return false
        if (tags != other.tags) return false
        if (domain != other.domain) return false
        if (accessExpiryDate != other.accessExpiryDate) return false
        if (legacyId != other.legacyId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + deal.hashCode()
        result = 31 * result + (role?.hashCode() ?: 0)
        result = 31 * result + tags.hashCode()
        result = 31 * result + (domain?.hashCode() ?: 0)
        result = 31 * result + (accessExpiryDate?.hashCode() ?: 0)
        return result
    }
}

class School(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val role: String?,
    override val tags: Set<OrganisationTag>,
    override val domain: String?,
    val district: District?,
    val externalId: ExternalOrganisationId?,
    features: Map<Feature, Boolean>?,
    legacyId: String? = null
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    role = role,
    tags = tags,
    domain = domain,
    features = features,
    legacyId = legacyId
) {
    override fun type(): OrganisationType {
        return OrganisationType.SCHOOL
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.district?.deal?.accessExpiresOn?.let { districtAccessExpiresOn ->
            this.deal.accessExpiresOn?.let { schoolAccessExpiresOn ->
                return if (districtAccessExpiresOn.isAfter(schoolAccessExpiresOn)) districtAccessExpiresOn else schoolAccessExpiresOn
            } ?: districtAccessExpiresOn
        } ?: this.deal.accessExpiresOn
}

class District(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: Set<OrganisationTag>,
    override val role: String?,
    override val domain: String?,
    features: Map<Feature, Boolean>?,
    val externalId: ExternalOrganisationId?,
    legacyId: String? = null
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    tags = tags,
    role = role,
    domain = domain,
    features = features,
    legacyId = legacyId
) {
    override fun type(): OrganisationType {
        return OrganisationType.DISTRICT
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.deal.accessExpiresOn
}

class ApiIntegration(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: Set<OrganisationTag>,
    override val role: String?,
    override val domain: String?,
    features: Map<Feature, Boolean>?,
    val allowsOverridingUserIds: Boolean,
    legacyId: String? = null
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    tags = tags,
    role = role,
    domain = domain,
    features = features,
    legacyId = legacyId
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.deal.accessExpiresOn
}

class LtiDeployment(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: Set<OrganisationTag>,
    override val role: String?,
    override val domain: String?,
    features: Map<Feature, Boolean>?,
    val deploymentId: String,
    val parent: Organisation,
    legacyId: String? = null
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    tags = tags,
    role = role,
    domain = domain,
    features = features,
    legacyId = legacyId
) {
    override fun type(): OrganisationType {
        return OrganisationType.LTI_DEPLOYMENT
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.deal.accessExpiresOn
}
