package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import java.time.ZonedDateTime

data class Organisation<T : OrganisationDetails>(
    val id: OrganisationId,
    val type: DealType,
    val accessExpiresOn: ZonedDateTime?,
    val details: T,
    val role: String?,
    val contentPackageId: ContentPackageId? = null
)

enum class DealType {
    DESIGN_PARTNER,
    STANDARD
}

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}
