package com.boclips.users.client.implementation.api.hateoas.contract;

import com.boclips.users.client.model.contract.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractsResource {
    private List<Contract> contracts;
}
