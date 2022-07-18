package org.paasta.container.terraman.api.common.repository;

import org.paasta.container.terraman.api.common.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    AccountModel findById(int id);
}
