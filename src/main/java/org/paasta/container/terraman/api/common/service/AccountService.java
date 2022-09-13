package org.paasta.container.terraman.api.common.service;

import org.paasta.container.terraman.api.common.model.AccountModel;
import org.paasta.container.terraman.api.common.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountModel getAccountInfo(int id) {
        AccountModel accountModel = null;
        try {
            accountModel = new AccountModel();
            accountModel = accountRepository.findById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return accountModel;
    }
}
