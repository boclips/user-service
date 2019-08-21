package com.boclips.users.application

import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.presentation.requests.MarketingTrackingRequest
import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserUpdatesConverterTest {
    private val fakeClient = FakeClient()
    private var userUpdatesConverter =
        UserUpdatesConverter(VideoServiceSubjectsClient(CacheableSubjectsClient(fakeClient)))

    @BeforeEach
    fun setUp() {
        fakeClient.clear()
        fakeClient.addSubject(Subject.builder().id("123").name("Maths").build())
    }

    @Test
    fun `converts first name change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(firstName = "Rebecca"))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts last name change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(lastName = "Rebecca"))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts subjects change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(subjects = listOf("123")))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts ages change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(ages = listOf(9, 10, 11)))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts marketing opt in change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(hasOptedIntoMarketing = true))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts referral code change to a command`() {
        val commands = userUpdatesConverter.convert(UpdateUserRequest(referralCode = "1234"))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts marketing tracking change to a command`() {
        val commands = userUpdatesConverter.convert(
            UpdateUserRequest(
                marketingTrackingRequest = MarketingTrackingRequest(
                    utmCampaign = "A",
                    utmTerm = "B",
                    utmMedium = "C",
                    utmContent = "D",
                    utmSource = "E"
                )
            )
        )

        assertThat(commands).hasSize(1)
    }
}