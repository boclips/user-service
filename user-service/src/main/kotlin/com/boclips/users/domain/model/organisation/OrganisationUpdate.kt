package com.boclips.users.domain.model.organisation

import java.time.ZonedDateTime

sealed class OrganisationUpdate {
    class ReplaceDealType(val type: DealType) : OrganisationUpdate()
    class ReplaceExpiryDate(val accessExpiresOn: ZonedDateTime) : OrganisationUpdate()
    class ReplaceDomain(val domain: String) : OrganisationUpdate()
    class AddTag(val tag: OrganisationTag) : OrganisationUpdate()
}
