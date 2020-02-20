package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.api.hateoas.accessrules.AccessRulesHateoasWrapper;
import com.boclips.users.client.implementation.api.hateoas.links.Links;
import com.boclips.users.client.implementation.api.hateoas.links.LinksResource;
import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;
import org.springframework.util.StringUtils;
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
    public List<AccessRule> getAccessRules(String userId) {
        return restTemplate
                .getForObject(getLinks().getAccessRules().getHref(), AccessRulesHateoasWrapper.class, userId)
                .get_embedded()
                .getAccessRules();
    }

    @Override
    public Account getAccount(String accountId) {
        try {
            return restTemplate.getForObject(getLinks().getAccount().getHref(), Account.class, accountId);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

    @Override
    public Boolean validateShareCode(String userId, String shareCode) {
        if (StringUtils.isEmpty(shareCode) || StringUtils.isEmpty(userId)) {
            return false;
        }

        try {
            return restTemplate
                    .getForEntity(getLinks().getValidateShareCode().getHref(), Object.class, userId, shareCode)
                    .getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    private Links getLinks() {
        if (links == null) {
            this.links = restTemplate.getForObject(baseUrl + "/v1", LinksResource.class).get_links();
        }

        return links;
    }
}
