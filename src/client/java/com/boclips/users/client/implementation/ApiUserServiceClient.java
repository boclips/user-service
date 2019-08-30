package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.api.hateoas.contract.ContractsHateoasWrapper;
import com.boclips.users.client.implementation.api.hateoas.links.Links;
import com.boclips.users.client.implementation.api.hateoas.links.LinksResource;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.contract.Contract;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ApiUserServiceClient implements UserServiceClient {
    private final String apiGatewayUrl;
    private final RestTemplate restTemplate;

    private Links links;

    public ApiUserServiceClient(String apiGatewayUrl, RestTemplate restTemplate) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public User findUser(String userId) {
        try {
            return restTemplate.getForObject(getLinks().getUser().getHref(), User.class, userId);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

    @Override
    public List<Contract> getContracts(String userId) {
        return restTemplate
                .getForObject(getLinks().getContracts().getHref(), ContractsHateoasWrapper.class, userId)
                .get_embedded()
                .getContracts();
    }

    private Links getLinks() {
        if (links == null) {
            this.links = restTemplate.getForObject(apiGatewayUrl + "/v1", LinksResource.class).get_links();
        }

        return links;
    }
}
