package com.boclips.users.domain.model.organisation

import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

class OrganisationTest {
    @Nested
    inner class School {
        @Test
        fun `access expires on comes from the school deal`() {
            val accessExpiresOn = LocalDate.of(2020, 10, 10).atStartOfDay(ZoneId.systemDefault())

            val school = OrganisationFactory.school(
                deal = OrganisationFactory.deal(
                    accessExpiresOn = accessExpiresOn
                )
            )

            assertThat(school.accessExpiryDate).isEqualTo(accessExpiresOn)
        }

        @Test
        fun `district access expiry date takes precedence over school expiry date`() {
            val schoolExpiryDate = LocalDate.of(2020, 10, 10).atStartOfDay(ZoneId.systemDefault())
            val districtExpiryDate = LocalDate.of(2010, 5, 5).atStartOfDay(ZoneId.systemDefault())

            val school = OrganisationFactory.school(
                deal = OrganisationFactory.deal(
                    accessExpiresOn = schoolExpiryDate
                ),
                district = OrganisationFactory.district(deal = OrganisationFactory.deal(accessExpiresOn = districtExpiryDate))
            )

            assertThat(school.accessExpiryDate).isEqualTo(districtExpiryDate)
        }
    }

    @Nested
    inner class District {
        @Test
        fun `access expires on comes from the deal`() {
            val accessExpiresOn = LocalDate.of(2020, 10, 10).atStartOfDay(ZoneId.systemDefault())

            val district = OrganisationFactory.district(
                deal = OrganisationFactory.deal(
                    accessExpiresOn = accessExpiresOn
                )
            )

            assertThat(district.accessExpiryDate).isEqualTo(accessExpiresOn)
        }
    }

    @Nested
    inner class ApiIntegration {
        @Test
        fun `access expires on comes from the deal`() {
            val accessExpiresOn = LocalDate.of(2020, 10, 10).atStartOfDay(ZoneId.systemDefault())

            val apiIntegration = OrganisationFactory.apiIntegration(
                deal = OrganisationFactory.deal(
                    accessExpiresOn = accessExpiresOn
                )
            )

            assertThat(apiIntegration.accessExpiryDate).isEqualTo(accessExpiresOn)
        }
    }
}
