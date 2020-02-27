package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.DealType
import com.boclips.users.domain.model.account.OrganisationType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "organisations")
data class OrganisationDocument(
        @Id
        val id: String?,
        val name: String,
        val role: String?,
        @Field("contractIds")
        val accessRuleIds: List<String> = emptyList(),
        val externalId: String?,
        val type: OrganisationType,
        val dealType: DealType?,
        val country: LocationDocument?,
        val state: LocationDocument?,
        val postcode: String?,
        val allowsOverridingUserIds: Boolean?,
        @DBRef
        val parentOrganisation: OrganisationDocument? = null,
        val accessExpiresOn: Instant? = null
)

data class LocationDocument(
    val code: String
)
