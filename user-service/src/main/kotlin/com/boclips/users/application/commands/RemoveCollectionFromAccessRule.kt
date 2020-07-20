package com.boclips.users.application.commands

import org.springframework.stereotype.Service

@Service
class RemoveCollectionFromAccessRule() {
    /*operator fun invoke(accessRuleId: AccessRuleId, collectionId: CollectionId) {
        accessRuleRepository.findById(accessRuleId)?.let { accessRule ->
            if (accessRule is AccessRule.IncludedCollections) {
                accessRule.collectionIds.filter { it != collectionId }.let {
                    val updatedAccessRule = accessRule.copy(collectionIds = it)
                    accessRuleRepository.save(updatedAccessRule)
                }
            }
        } ?: throw AccessRuleNotFoundException(accessRuleId.value)
    }*/
}
