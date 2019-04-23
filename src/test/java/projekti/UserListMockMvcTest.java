package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class UserListMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;  
    
    @Autowired
    private AccountRepository accountRepository;
        
    @Autowired
    PasswordEncoder passwordEncoder;
  
    
    @Before
    public void initTestUsers() {
        for(int id = 0; id <= 10; id++) {
            if(accountRepository.findByProfilename("test" + (id + 201)) == null) {
                Account test = createTestUser(id + 201);
                accountRepository.save(test);
            }
        }
    }
    
    @After
    public void removeTestUsers() {
        for(int id = 0; id <= 10; id++) {
            Account test = accountRepository.findByProfilename("test" + (id + 201));
            accountRepository.delete(test);
        }
    }
    
    private Account createTestUser(int id) {
        Account test = new Account();
        test.setRealname("Userlist MockMvc Testaaja (test" + id + ")");
        test.setUsername("testi" + id);
        test.setProfilename("test" + id);
        test.setProfilePicture(null) ;
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
          
    @Test
    @WithMockUser(username = "testi201")
    public void loggedUserCanFindTestUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat").param("findname","Userlist MockMvc Testaaja")).andReturn();
        List<Account> users = (List)result.getModelAndView().getModel().get("userlist");
        assertTrue("Test users visible on userlist", users.size() == 11); 
    } 
    
    @Test
    @WithMockUser(username = "testi201")
    public void loggedUserGetsErrorWhenTryingToFindnonexistingAccount() throws Exception {
        Account test = accountRepository.findByProfilename("UserlistMockMvcTestaajaZXCV");
        if(test != null) accountRepository.delete(test);
        
        MvcResult result = mockMvc.perform(get("/kayttajat").param("findname","UserlistMockMvcTestaajaZXCV")).andReturn();
        List<Account> users = (List)result.getModelAndView().getModel().get("userlist");
        assertTrue("No users should be found on userlist", users.size() == 0); 
     
        String error = result.getModelAndView().getModel().get("FindUserError").toString();
        assertTrue("No users should be on userlist", error != null); 
    }
    
    @Test
    public void anonymousUserCannotSeeFriendList() throws Exception {
        mockMvc.perform(get("/kayttajat")).andExpect(redirectedUrl("http://localhost/login"));
    } 
    
}
