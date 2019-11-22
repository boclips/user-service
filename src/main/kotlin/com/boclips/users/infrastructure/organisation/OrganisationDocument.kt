package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.organisation.OrganisationType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organisations")
data class OrganisationDocument(
        @Id
        val id: String?,
        val name: String,
        val role: String?,
        val contractIds: List<String> = emptyList(),
        val externalId: String?,
        val type: OrganisationType,
        val accountType: OrganisationAccountType?,
        val country: LocationDocument?,
        val state: LocationDocument?,
        val postcode: String?,
        @DBRef
        val parentOrganisation: OrganisationDocument? = null
)

data class LocationDocument(
    val code: String
)
