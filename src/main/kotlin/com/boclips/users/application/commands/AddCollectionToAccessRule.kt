package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Service

@Service
class AddCollectionToAccessRule(
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(accessRuleId: AccessRuleId, collectionId: CollectionId) {
        accessRuleRepository
            .findById(accessRuleId)
            ?.let {
                if (it is AccessRule.SelectedCollections) {
                    val updatedAccessRule = it.copy(
                        collectionIds = it.collectionIds.toMutableSet().apply { add(collectionId) }.toList()
                    )
                    accessRuleRepository.save(updatedAccessRule)
                }
            } ?: throw AccessRuleNotFoundException(accessRuleId.value)
    }
}
