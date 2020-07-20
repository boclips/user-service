package com.boclips.users.api.factories

import com.boclips.users.api.response.accessrule.AccessRuleResource

class AccessRuleResourceFactory {
    companion object {
        @JvmStatic
        fun sampleIncludedCollections(
            id: String = "access-rule-id",
            name: String = "collection",
            collectionIds: List<String> = emptyList()
        ): AccessRuleResource {
            return AccessRuleResource.IncludedCollections(
                id = id,
                name = name,
                collectionIds = collectionIds
            )
        }
    }
}
