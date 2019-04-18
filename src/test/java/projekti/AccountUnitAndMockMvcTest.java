package projekti;

import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountUnitAndMockMvcTest  {
      
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
    
    @After
    public void undo() {            
        Account test = accountRepository.findByProfilename("test1");
        if( test != null) {
            accountRepository.delete(test);
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
        Account test = new Account();
        if(accountRepository.findByProfilename("test" + idForNextTestUser) == null) {
            test = createTestUser(idForNextTestUser);
            idForNextTestUser++;
            accountRepository.save(test);
        }
        assertTrue("One account can be added", accountRepository.findAll().size() == accountsBefore + 1);   
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
    public void statusOkforAccountPage() throws Exception {
        mockMvc.perform(get("/accounts")).andExpect(status().isOk());
    } 
    
    @Test
    @WithMockUser(username = "test1", password = "test12345")
    public void statusOkforOmasivu() throws Exception {
        mockMvc.perform(get("/omasivu")).andExpect(redirectedUrl("/kayttajat/test1")).andExpect(status().isFound());
    } 
    
    @Test
    @WithMockUser(username = "test1", password = "test12345")
    public void statusOkforKayttajaTest1PageWithOwn() throws Exception {
        mockMvc.perform(get("/kayttajat/test1")).andExpect(status().isOk());
    } 

    @Test
    @WithMockUser(username = "test2", password = "test12345")
    public void statusOkforKayttajaTest1PageWithOtherAccount() throws Exception {
        mockMvc.perform(get("/kayttajat/test1")).andExpect(status().isOk());
    }     
    @Test
    @WithMockUser(username = "test1", password = "test1234")
    public void modelOkforKayttajaTest1Page() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test1")).andReturn();
        String message = result.getModelAndView().getModel().get("IsMyPage").toString();
        assertTrue("Model includes IsMyPage" , message != null);
    } 
    
    @Test
    @WithMockUser(username = "test2", password = "test1234")
    public void modelForKayttajaTest1PageWithOtherAccount() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test1")).andReturn();
        List<String> models= (List)result.getModelAndView().getModel().get("IsMyPage");
        assertTrue("Model should not include IsMyPage" , models == null);
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
