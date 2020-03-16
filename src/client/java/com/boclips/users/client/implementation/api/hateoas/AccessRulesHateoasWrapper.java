package com.boclips.users.client.implementation.api.hateoas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRulesHateoasWrapper {
    private AccessRulesResource _embedded;
}
