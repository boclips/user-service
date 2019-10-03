package com.boclips.users.presentation.controllers.contract

import com.boclips.users.application.commands.AddCollectionToContract
import com.boclips.users.application.commands.RemoveCollectionFromContract
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.ContractId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/selected-content-contracts")
class SelectedContentContractController(
    private val addCollectionToContract: AddCollectionToContract,
    private val removeCollectionFromContract: RemoveCollectionFromContract
) {
    @PutMapping("/{contractId}/collections/{collectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addCollection(@PathVariable contractId: String, @PathVariable collectionId: String) {
        addCollectionToContract(ContractId(contractId), CollectionId(collectionId))
    }

    @DeleteMapping("/{contractId}/collections/{collectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCollection(@PathVariable contractId: String, @PathVariable collectionId: String) {
        removeCollectionFromContract(ContractId(contractId), CollectionId(collectionId))
    }
}