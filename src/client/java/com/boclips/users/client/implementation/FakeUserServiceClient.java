package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.contract.Contract;

import java.util.ArrayList;
import java.util.List;

public class FakeUserServiceClient implements UserServiceClient {
    private User user = null;
    private List<Contract> contracts = new ArrayList<>();

    @Override
    public User findUser(String userId) {
        return user;
    }

    @Override
    public List<Contract> getContracts(String userId) {
        return contracts;
    }

    public User addUser(User user) {
        this.user = user;
        return user;
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }
}
