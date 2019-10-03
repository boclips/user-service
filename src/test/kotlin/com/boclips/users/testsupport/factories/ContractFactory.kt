package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId
import org.bson.types.ObjectId

class ContractFactory {
    companion object {
        fun sampleSelectedContentContract(
            id: ContractId = ContractId(ObjectId().toHexString()),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = Contract.SelectedContent(id, name, collectionIds)
    }
}
