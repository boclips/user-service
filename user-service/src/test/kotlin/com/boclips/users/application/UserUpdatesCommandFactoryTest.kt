package com.boclips.users.application

import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.api.request.user.MarketingTrackingRequest
import com.boclips.users.api.request.user.UpdateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.boclips.videos.api.request.subject.CreateSubjectRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

class UserUpdatesCommandFactoryTest : AbstractSpringIntegrationTest() {

    lateinit var userUpdatesConverter: UserUpdatesCommandFactory

    @BeforeEach
    fun setUp() {
        val fakeClient = SubjectsClientFake()
        fakeClient.create(CreateSubjectRequest("some-subject"))

        userUpdatesConverter =
            UserUpdatesCommandFactory(VideoServiceSubjectsClient(CacheableSubjectsClient(fakeClient)))
    }

    @Test
    fun `converts first name change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                firstName = "Rebecca"
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts last name change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                lastName = "Rebecca"
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts subjects change to a command`() {
        val fakeClient = SubjectsClientFake()
        fakeClient.create(CreateSubjectRequest("some-subject"))
        val subjectId = fakeClient.getSubjects()._embedded.subjects.stream().collect(Collectors.toList()).first().id

        userUpdatesConverter =
            UserUpdatesCommandFactory(VideoServiceSubjectsClient(CacheableSubjectsClient(fakeClient)))

        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                subjects = listOf(subjectId)
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts ages change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                ages = listOf(9, 10, 11)
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts marketing opt in change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                hasOptedIntoMarketing = true
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts referral code change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                referralCode = "1234"
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts marketing tracking change to a command`() {
        val commands = userUpdatesConverter.buildCommands(
            UpdateUserRequest(
                utm = MarketingTrackingRequest(
                    campaign = "A",
                    term = "B",
                    medium = "C",
                    content = "D",
                    source = "E"
                )
            )
        )

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts organisation change to a command`() {
        val organisation = OrganisationFactory.school()
        val commands = userUpdatesConverter.buildCommands(UpdateUserRequest(), organisation)

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts user role change to a command`() {
        val commands = userUpdatesConverter.buildCommands(UpdateUserRequest(role = "TEACHER"))

        assertThat(commands).hasSize(1)
    }
}
