package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.SelectedContentContract
import java.util.UUID

class SelectedContentContractFactory {
    companion object {
        fun sample(
            id: ContractId = ContractId(UUID.randomUUID().toString()),
            name: String = "Tailored collections list",
            collectionIds: List<CollectionId> = emptyList()
        ) = SelectedContentContract(id, name, collectionIds)
    }
}
