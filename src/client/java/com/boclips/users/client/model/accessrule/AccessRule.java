package com.boclips.users.client.model.accessrule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IncludedCollectionsAccessRule.class, name = "IncludedCollections"),
        @JsonSubTypes.Type(value = IncludedVideosAccessRule.class, name = "IncludedVideos"),
        @JsonSubTypes.Type(value = ExcludedVideosAccessRule.class, name = "ExcludedVideos"),
        @JsonSubTypes.Type(value = ExcludedVideoTypesAccessRule.class, name = "ExcludedVideoTypes")
})
@Data
public abstract class AccessRule {
    protected String name;
}
