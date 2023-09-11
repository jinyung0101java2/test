package org.container.terraman.api.common.service;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.container.terraman.api.common.model.AccountModel;
import org.container.terraman.api.common.repository.AccountRepository;
import org.container.terraman.api.common.util.CommonUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class AccountServiceTest {

    private static final int TEST_ID = 1;
    private AccountModel accountModelMock;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Before
    public void setUp(){
        accountModelMock = new AccountModel();
        accountModelMock.setProvider("testProvider");
        accountModelMock.setProject("testProject");
        accountModelMock.setRegion("testRegion");
        accountModelMock.setName("testName");
        accountModelMock.setId(1);
    }

    @Test
    public void getAccountInfoTest() throws Exception{
        when(accountRepository.findById(TEST_ID)).thenReturn(accountModelMock);
        when(accountRepository.findById(TEST_ID)).thenThrow(Exception.class);

        AccountModel result = new AccountModel();
        try {
            result = accountService.getAccountInfo(TEST_ID);
        } catch (Exception e) {
            System.out.println("e = " + e);
        }


        assertEquals(0, result.getId());
    }
}
