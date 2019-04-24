package projekti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountUnitTest  {
       
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private int idForNextTestUser = 1;
    private int idForLastAddedUser = 1;
    private int accountsBeforeTest = 0;
    
    @Before
    public void init() {
        accountsBeforeTest = accountRepository.findAll().size();
        if(accountRepository.findByProfilename("test1") == null) {
            Account test = createTestUser(1);
            accountRepository.save(test);
        }
        while(accountRepository.findByProfilename("test" + idForNextTestUser) != null) {
            idForNextTestUser++;
        }
    }
    
    private Account createTestUser(int id) {
        Account test = new Account();
        test.setRealname("Account Unit Testaaja (test" + id + ")");
        test.setUsername("testi" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }

    @Test
    public void canAddOneTestAccount() {
        int accountsBefore = accountRepository.findAll().size();
        Account test = new Account();
        if(accountRepository.findByProfilename("test" + idForNextTestUser) == null) {
            test = createTestUser(idForNextTestUser);
            idForNextTestUser++;
            accountRepository.save(test);
        }
        assertTrue("One account can be added", accountRepository.findAll().size() == accountsBefore + 1);   
        assertTrue("Can count number of accounts", accountService.numberOfAccounts() == accountsBefore + 1);
        accountRepository.delete(test);      
    }
    
    @Test
    public void canAddManyTestAccounts() {
        int[] id = new int[5];
        for(int counter = 0; counter < 3; counter++) {
            while(accountRepository.findByProfilename("test" + idForNextTestUser) != null) {
                idForNextTestUser++;
            }
            Account test = createTestUser(idForNextTestUser);
            id[counter] = idForNextTestUser;
            idForLastAddedUser = idForNextTestUser;
            accountRepository.save(test);
        }
        assertTrue("Many accounts can be added", accountRepository.findByProfilename("test" + idForLastAddedUser) != null); 
        for(int counter = 0; counter < 3; counter++) {
            Account test = accountRepository.findByProfilename("test" + id[counter] );
            accountRepository.delete(test);
        }
    }
    
    @Test
    public void testForMethodAccountIsOkForExistingAccount() {
        Account account = accountRepository.findByProfilename("test" + idForLastAddedUser);
        assertFalse("Method accountIsOk", accountService.accountIsOkToBeAdded(account));           
    }
    
    @Test
    public void testForMethodAccountIsOkForNewAccount() {
        while(accountRepository.findByProfilename("test" + idForNextTestUser) != null) {
                idForNextTestUser++;
            }
        Account account = createTestUser(idForNextTestUser);
        assertTrue("Method accountIsOk", accountService.accountIsOkToBeAdded(account));           
    }
    
}
