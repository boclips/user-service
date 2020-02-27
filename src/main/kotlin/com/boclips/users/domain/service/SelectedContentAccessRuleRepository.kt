package com.boclips.users.domain.service

import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.accessrules.CollectionId
import com.boclips.users.domain.model.accessrules.VideoId

// TODO Potentially we'd be able to remove this dedicated repository and persist through ContractRepository
// in a more generic manner. Can be revisited once we introduce API endpoints for creating contracts.
interface SelectedContentAccessRuleRepository {
    fun saveSelectedCollectionsAccessRule(name: String, collectionIds: List<CollectionId>): AccessRule.SelectedCollections
    fun saveSelectedVideosAccessRule(name: String, videoIds: List<VideoId>): AccessRule.SelectedVideos
}