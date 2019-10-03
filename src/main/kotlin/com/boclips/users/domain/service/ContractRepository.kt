package com.boclips.users.domain.service

import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.contract.ContractId

interface ContractRepository {
    fun findById(id: ContractId): Contract?
    fun findAll(): List<Contract>
    fun findAllByName(name: String): List<Contract>
    fun save(contract: Contract)
}
