package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.feature.Feature
import java.time.ZonedDateTime

sealed class OrganisationUpdate {
    class ReplaceExpiryDate(val accessExpiresOn: ZonedDateTime) : OrganisationUpdate()
    class ReplaceDomain(val domain: String) : OrganisationUpdate()
    class ReplaceFeatures(val features: Map<Feature, Boolean>) : OrganisationUpdate()
    class AddTag(val tag: OrganisationTag) : OrganisationUpdate()
    class ReplaceBilling(val billing: Boolean) : OrganisationUpdate()
    class ReplaceContentPackageId(val contentPackageId: ContentPackageId) : OrganisationUpdate()
}
