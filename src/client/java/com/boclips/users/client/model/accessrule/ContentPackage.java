package com.boclips.users.client.model.accessrule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackage {
    private String id;
    private String name;
    private List<AccessRule> accessRules;
}
