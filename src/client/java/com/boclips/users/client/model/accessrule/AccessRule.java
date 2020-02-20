package com.boclips.users.client.model.accessrule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectedCollectionsAccessRule.class, name = "SelectedCollections"),
        @JsonSubTypes.Type(value = SelectedVideosAccessRule.class, name = "SelectedVideos")
})
@Data
public abstract class AccessRule {
    protected String name;
}
