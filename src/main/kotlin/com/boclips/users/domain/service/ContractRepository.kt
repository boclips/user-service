package com.boclips.users.domain.service

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId

interface ContractRepository {
    fun findById(id: ContractId): Contract?
    fun findByName(name: String): Contract?
}
