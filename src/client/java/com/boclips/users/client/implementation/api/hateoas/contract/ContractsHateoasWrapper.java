package com.boclips.users.client.implementation.api.hateoas.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractsHateoasWrapper {
    private ContractsResource _embedded;
}
