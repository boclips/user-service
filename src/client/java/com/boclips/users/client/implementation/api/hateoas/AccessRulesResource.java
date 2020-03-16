package com.boclips.users.client.implementation.api.hateoas;

import com.boclips.users.client.model.accessrule.AccessRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRulesResource {
    private List<AccessRule> accessRules;
}
