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
    open val domain: String?
) {
    abstract fun type(): OrganisationType
}

data class School(
    override val id: OrganisationId,
    override val deal: Deal,
    override val role: String?,
    override val name: String,
    override val address: Address,
    override val domain: String?,
    val district: District?,
    val externalId: ExternalOrganisationId?
) : Organisation(
    id = id,
    deal = deal,
    role = role,
    name = name,
    address = address,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.SCHOOL
    }
}

data class District(
    override val id: OrganisationId,
    override val deal: Deal,
    override val role: String?,
    override val name: String,
    override val address: Address,
    override val domain: String?,
    val externalId: ExternalOrganisationId?
) : Organisation(
    id = id,
    deal = deal,
    role = role,
    name = name,
    address = address,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.DISTRICT
    }
}

data class ApiIntegration(
    override val id: OrganisationId,
    override val deal: Deal,
    override val role: String?,
    override val name: String,
    override val address: Address,
    override val domain: String?,
    val allowsOverridingUserIds: Boolean
) : Organisation(
    id = id,
    deal = deal,
    role = role,
    name = name,
    address = address,
    domain = domain
) {
    override fun type(): OrganisationType {
        return OrganisationType.API
    }
}
