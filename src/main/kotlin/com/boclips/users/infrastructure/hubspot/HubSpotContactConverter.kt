package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.marketing.CrmProfile
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class HubSpotContactConverter {
    fun convert(crmProfile: CrmProfile): HubSpotContact {
        return HubSpotContact(
            email = crmProfile.email,
            properties = listOfNotNull(
                HubSpotProperty("firstname", crmProfile.firstName),
                HubSpotProperty("lastname", crmProfile.lastName),
                HubSpotProperty("is_b2t", "true"),
                HubSpotProperty("b2t_is_activated", crmProfile.activated.toString()),
                HubSpotProperty("subjects_taught", crmProfile.subjects.joinToString { it.name }),
                HubSpotProperty("age_range", crmProfile.ageRange.joinToString()),
                HubSpotProperty("b2t_utm_source", crmProfile.marketingTracking.utmSource),
                HubSpotProperty("b2t_utm_term", crmProfile.marketingTracking.utmTerm),
                HubSpotProperty("b2t_utm_content", crmProfile.marketingTracking.utmContent),
                HubSpotProperty("b2t_utm_medium", crmProfile.marketingTracking.utmMedium),
                HubSpotProperty("b2t_utm_campaign", crmProfile.marketingTracking.utmCampaign),
                HubSpotProperty("b2t_last_logged_in", convertToInstantAtMidnight(crmProfile.lastLoggedIn)),
                HubSpotProperty("b2t_access_expiry", convertToInstantAtMidnight(crmProfile.accessExpiresOn))
            )
        )
    }

    private fun convertToInstantAtMidnight(instant: Instant?): String {
        return instant?.let {
            ZonedDateTime
                .ofInstant(it, ZoneId.of("UTC"))
                .toLocalDate()
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
                .toString()
        } ?: ""
    }
}
