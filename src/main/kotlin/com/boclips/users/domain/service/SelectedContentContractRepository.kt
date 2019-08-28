package com.boclips.users.domain.service

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.contract.SelectedContentContract

interface SelectedContentContractRepository {
    fun saveSelectedContentContract(name: String, collectionIds: List<CollectionId>): SelectedContentContract
    fun findById(id: ContractId): SelectedContentContract?
}
