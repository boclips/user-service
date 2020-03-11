package com.boclips.users.client.model.accessrule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExcludedVideoTypesAccessRule extends AccessRule {
    private List<String> videoTypes;
}
