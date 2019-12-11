package com.boclips.users.client.model.contract;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectedCollectionsContract.class, name = "SelectedCollections"),
        @JsonSubTypes.Type(value = SelectedVideosContract.class, name = "SelectedVideos")
})
@Data
public abstract class Contract {
    protected String name;
}
