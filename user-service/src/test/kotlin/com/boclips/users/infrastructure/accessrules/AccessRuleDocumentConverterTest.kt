package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPartnerId
import com.boclips.users.domain.model.contentpackage.DistributionMethod
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.service.UniqueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class AccessRuleDocumentConverterTest {

    private val converter = AccessRuleDocumentConverter()

    class AccessRuleProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?) = Stream.of(
            AccessRule.IncludedCollections(
                id = AccessRuleId(UniqueId()),
                name = "included cols",
                collectionIds = listOf(
                    CollectionId("collection-1")
                )
            ),
            AccessRule.IncludedVideos(
                id = AccessRuleId(UniqueId()),
                name = "included vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedVideos(
                id = AccessRuleId(UniqueId()),
                name = "excluded vids",
                videoIds = listOf(
                    VideoId("video-1")
                )
            ),
            AccessRule.ExcludedContentPartners(
                id = AccessRuleId(UniqueId()),
                name = "excluded CPs",
                contentPartnerIds = listOf(
                    ContentPartnerId("cp-1")
                )
            ),
            AccessRule.IncludedDistributionMethods(
                id = AccessRuleId(UniqueId()),
                name = "included distr methods",
                distributionMethods = setOf(DistributionMethod.DOWNLOAD, DistributionMethod.STREAM)
            )
        ).map { accessRule -> Arguments.of(accessRule) }
    }

    @ParameterizedTest
    @ArgumentsSource(AccessRuleProvider::class)
    fun `convert an access rule to document and back`(originalRule: AccessRule) {
        val retrievedRule = originalRule
            .let(converter::toDocument)
            .let(converter::fromDocument)

        assertThat(retrievedRule).isEqualTo(originalRule)
    }
}
