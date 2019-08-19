package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.videos.service.client.Subject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists account`() {
        val account = AccountFactory.sample()
        userRepository.save(account)

        val fetchedUser = userRepository.findById(account.id)!!

        assertThat(fetchedUser.id).isNotNull()
        assertThat(fetchedUser.account).isEqualTo(account)
    }

    @Test
    fun `persists user`() {
        val user = UserFactory.sample(
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

        val fetchedUser = userRepository.findById(user.id)

        assertThat(fetchedUser).isEqualTo(user)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            userRepository.save(UserFactory.sample()),
            userRepository.save(UserFactory.sample())
        )

        assertThat(userRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `count users`() {
        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample()
            )
        )
        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-2"
                ),
                profile = ProfileFactory.sample()
            )
        )
        userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-3"
                ),
                profile = null
            )
        )

        val counts = userRepository.count()

        assertThat(counts.total).isEqualTo(3)
        assertThat(counts.activated).isEqualTo(2)
    }
}
