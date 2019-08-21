package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.api.hateoas.Links;
import com.boclips.users.client.implementation.api.hateoas.LinksResource;
import com.boclips.users.client.model.User;
import org.springframework.web.client.RestTemplate;

public class ApiUserServiceClient implements UserServiceClient {
    private final RestTemplate restTemplate;

    private Links links;

    public ApiUserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public User findUser(String userId) {
        return restTemplate.getForObject(getLinks().getProfile().getHref(), User.class, userId);
    }

    private Links getLinks() {
        if (links == null) {
            this.links = restTemplate.getForObject("/v1", LinksResource.class).get_links();
        }

        return links;
    }
}
