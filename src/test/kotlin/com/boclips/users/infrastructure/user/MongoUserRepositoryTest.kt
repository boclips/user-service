package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.MarketingTrackingFactory
import com.boclips.users.testsupport.OrganisationIdFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists compulsory information`() {
        val user = AccountFactory.sample(organisationId = OrganisationIdFactory.sample())

        userRepository.save(user)

        val fetchedUser = userRepository.findById(user.id)!!

        assertThat(fetchedUser.id).isNotNull()
        assertThat(fetchedUser.firstName).isNotNull()
        assertThat(fetchedUser.lastName).isNotNull()
        assertThat(fetchedUser.email).isNotNull()
        assertThat(fetchedUser.activated).isNotNull()
        assertThat(fetchedUser.organisationId).isNotNull()
    }

    @Test
    fun `persists optional information`() {
        val user = AccountFactory.sample(
            subjects = listOf(Subject(id = SubjectId(value = "1"), name = "Maths")),
            ageRange = listOf(1, 2, 3, 4),
            marketing = MarketingTrackingFactory.sample(
                utmCampaign = "campaign",
                utmSource = "source",
                utmMedium = "medium",
                utmContent = "content",
                utmTerm = "term"
            ),
            referralCode = "referral-123",
            analyticsId = AnalyticsId(value = "analytics-123")
        )

        userRepository.save(user)

        val fetchedUser = userRepository.findById(user.id)!!

        assertThat(fetchedUser.hasOptedIntoMarketing).isNotNull()
        assertThat(fetchedUser.ageRange).isNotNull()
        assertThat(fetchedUser.subjects).isNotNull()
        assertThat(fetchedUser.marketingTracking).isNotNull()
        assertThat(fetchedUser.referralCode).isNotNull()
        assertThat(fetchedUser.analyticsId).isNotNull()
    }

    @Test
    fun `saving organisation id as null is all good`() {
        val account = AccountFactory.sample(
            subjects = listOf(Subject(id = SubjectId(value = "1"), name = "Maths")),
            organisationId = null
        )

        userRepository.save(account)

        val fetchedUser = userRepository.findById(account.id)

        assertThat(fetchedUser).isEqualTo(account)
        assertThat(fetchedUser!!.organisationId).isNull()
    }

    @Test
    fun `saving analytics id as null is all good`() {
        val account = AccountFactory.sample(
            subjects = listOf(Subject(id = SubjectId(value = "1"), name = "Maths")),
            analyticsId = null
        )

        userRepository.save(account)

        assertThat(userRepository.findById(account.id)).isEqualTo(account)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample())
        )

        assertThat(userRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `activate an account`() {
        val account = AccountFactory.sample(
            activated = false,
            analyticsId = null
        )

        userRepository.save(account)
        userRepository.activate(account.id)

        assertThat(userRepository.findById(account.id)!!.activated).isTrue()
    }

    @Test
    fun `count users`() {
        userRepository.save(
            AccountFactory.sample(
                id = "user-1",
                activated = false,
                analyticsId = null
            )
        )
        userRepository.save(
            AccountFactory.sample(
                id = "user=2",
                activated = false,
                analyticsId = null
            )
        )
        userRepository.activate(UserId("user-1"))

        val counts = userRepository.count()

        assertThat(counts.total).isEqualTo(2)
        assertThat(counts.activated).isEqualTo(1)
    }
}
