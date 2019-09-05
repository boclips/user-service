package com.boclips.users.infrastructure.contract

import org.springframework.data.mongodb.repository.MongoRepository

interface ContractDocumentMongoRepository : MongoRepository<ContractDocument, String> {
    fun findByName(name: String): ContractDocument?
}