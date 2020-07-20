package com.boclips.users.application.commands

import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import org.springframework.stereotype.Service

@Service
class AddCollectionToAccessRule(

) {
    operator fun invoke(accessRuleId: AccessRuleId, collectionId: CollectionId) {
//        accessRuleRepository
//            .findById(accessRuleId)
//            ?.let {
//                if (it is AccessRule.IncludedCollections) {
//                    val updatedAccessRule = it.copy(
//                        collectionIds = it.collectionIds.toMutableSet().apply { add(collectionId) }.toList()
//                    )
//                    accessRuleRepository.save(updatedAccessRule)
//                }
//            } ?: throw AccessRuleNotFoundException(accessRuleId.value)
    }
}
