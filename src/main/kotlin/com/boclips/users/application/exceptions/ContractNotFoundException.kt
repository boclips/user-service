package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.contract.ContractId

class ContractNotFoundException(val contractId: ContractId) : RuntimeException("Contract not found for $contractId")