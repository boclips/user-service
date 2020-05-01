package com.boclips.users.domain.model.organisation

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}

sealed class Organisation(
    open val id: OrganisationId,
    open val name: String,
    open val address: Address,
    open val deal: Deal,
    open val role: String?,
    open val tags: List<OrganisationTag>,
    open val domain: String?
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val role: String?,
    override val tags: List<OrganisationTag>,
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
}

data class District(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: List<OrganisationTag>,
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
}

data class ApiIntegration(
    override val id: OrganisationId,
    override val name: String,
    override val address: Address,
    override val deal: Deal,
    override val tags: List<OrganisationTag>,
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
}
