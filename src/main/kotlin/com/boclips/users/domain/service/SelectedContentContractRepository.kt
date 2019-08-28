package com.boclips.users.domain.service

import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.Contract

// TODO Potentially we'd be able to remove this dedicated repository and persist through ContractRepository
// in a more generic manner. Can be revisited once we introduce API endpoints for creating contracts.
interface SelectedContentContractRepository {
    fun saveSelectedContentContract(name: String, collectionIds: List<CollectionId>): Contract.SelectedContent
}
