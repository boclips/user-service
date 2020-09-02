package com.boclips.users.domain.model.organisation

import java.time.ZonedDateTime

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}

sealed class Organisation(
    open val id: OrganisationId,
    open val name: String,
    open val address: Address,
    open val deal: Deal,
    open val role: String?,
    open val tags: Set<OrganisationTag>,
    open val domain: String?
) {
    abstract fun type(): OrganisationType
    abstract val accessExpiryDate: ZonedDateTime?
}

data class School(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val role: String?,
    override val tags: Set<OrganisationTag>,
    override val domain: String?,
    val district: District?,
    val externalId: ExternalOrganisationId?
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    role = role,
    tags = tags,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.SCHOOL
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.district?.deal?.accessExpiresOn ?: this.deal.accessExpiresOn
}

data class District(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: Set<OrganisationTag>,
    override val role: String?,
    override val domain: String?,
    val externalId: ExternalOrganisationId?
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    tags = tags,
    role = role,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.DISTRICT
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.deal.accessExpiresOn
}

data class ApiIntegration(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: Set<OrganisationTag>,
    override val role: String?,
    override val domain: String?,
    val allowsOverridingUserIds: Boolean
) : Organisation(
    id = id,
    name = name,
    address = address,
    deal = deal,
    tags = tags,
    role = role,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }

    override val accessExpiryDate: ZonedDateTime?
        get() = this.deal.accessExpiresOn
}
