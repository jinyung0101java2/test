package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountModel getAccountInfo(int id) {
        return accountRepository.findById(id);
    }
}
