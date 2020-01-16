package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.contract.Contract;

import java.util.ArrayList;
import java.util.List;

public class FakeUserServiceClient implements UserServiceClient {
    private User user = null;
    private List<Contract> contracts = new ArrayList<>();
    private Account account = null;

    @Override
    public User findUser(String userId) {
        return user;
    }

    public User addUser(User user) {
        this.user = user;
        return user;
    }

    @Override
    public List<Contract> getContracts(String userId) {
        return contracts;
    }

    @Override
    public Account getAccount(String accountId) {
        return account;
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public void clearUser() {
        this.user = null;
    }

    public void clearAccount() {
        this.account = null;
    }

    public void clearContracts() {
        contracts.clear();
    }
}
