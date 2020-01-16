package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.api.hateoas.contract.ContractsHateoasWrapper;
import com.boclips.users.client.implementation.api.hateoas.links.Links;
import com.boclips.users.client.implementation.api.hateoas.links.LinksResource;
import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.contract.Contract;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ApiUserServiceClient implements UserServiceClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    private Links links;

    public ApiUserServiceClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
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

    @Override
    public Account getAccount(String accountId) {
        try {
            return restTemplate.getForObject(getLinks().getAccount().getHref(), Account.class, accountId);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

    private Links getLinks() {
        if (links == null) {
            this.links = restTemplate.getForObject(baseUrl + "/v1", LinksResource.class).get_links();
        }

        return links;
    }
}
