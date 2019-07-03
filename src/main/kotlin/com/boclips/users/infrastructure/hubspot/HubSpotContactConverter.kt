package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.marketing.CrmProfile
import java.time.LocalDateTime
import java.time.ZoneId

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
                HubSpotProperty("b2t_last_logged_in", convertToInstantAtMidnight(crmProfile))
            )
        )
    }

    private fun convertToInstantAtMidnight(crmProfile: CrmProfile): String {
        return crmProfile.lastLoggedIn?.let { lastLogin ->
            LocalDateTime
                .ofInstant(lastLogin, ZoneId.of("UTC"))
                .toLocalDate()
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
                .toString()
        } ?: ""
    }
}