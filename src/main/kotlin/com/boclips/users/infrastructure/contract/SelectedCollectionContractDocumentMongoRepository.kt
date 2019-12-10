package com.boclips.users.infrastructure.contract

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedCollectionContractDocumentMongoRepository :
    MongoRepository<ContractDocument.SelectedCollections, String>