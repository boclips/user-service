package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.DealType
import java.time.ZonedDateTime

sealed class OrganisationUpdate {
    class ReplaceDealType(val type: DealType) : OrganisationUpdate()
    class ReplaceExpiryDate(val accessExpiresOn: ZonedDateTime) : OrganisationUpdate()
    class ReplaceDomain(val domain: String) : OrganisationUpdate()
}
