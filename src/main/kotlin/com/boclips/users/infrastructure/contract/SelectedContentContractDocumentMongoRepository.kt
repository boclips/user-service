package com.boclips.users.infrastructure.contract

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedContentContractDocumentMongoRepository : MongoRepository<ContractDocument.SelectedContent, String>