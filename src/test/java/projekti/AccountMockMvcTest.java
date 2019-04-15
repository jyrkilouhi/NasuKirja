package projekti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private int idForNextTestUser = 1;
    private int idForLastAddedUser = 1;
    
    @Before
    public void init() {
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
        test.setRealname("AccountMock Testaaja (test" + id + ")");
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }

    @Test
    public void canAddOneTestAccount() {
        int accountsBefore = accountRepository.findAll().size();
        if(accountRepository.findByProfilename("test" + idForNextTestUser) == null) {
            Account test = createTestUser(idForNextTestUser);
            idForNextTestUser++;
            accountRepository.save(test);
        }
        assertTrue("One account can be added", accountRepository.findAll().size() == accountsBefore + 1);   
    }
    
    @Test
    public void canAddManyTestAccounts() {
        for(int counter = 0; counter < 3; counter++) {
            while(accountRepository.findByProfilename("test" + idForNextTestUser) != null) {
                idForNextTestUser++;
            }
            Account test = createTestUser(idForNextTestUser);
            idForLastAddedUser = idForNextTestUser;
            accountRepository.save(test);
        }
        assertTrue("Many accounts can be added", accountRepository.findByProfilename("test" + idForLastAddedUser) != null);   
    }
    
    @Test
    public void statusOkforAccountPage() throws Exception {
        mockMvc.perform(get("/accounts")).andExpect(status().isOk());
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
