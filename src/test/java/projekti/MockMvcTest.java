package projekti;

import static com.google.common.collect.Range.greaterThan;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.server.Session.Cookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("Testaaja" + id);
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }

    @Test
    public void canMakeOneTestAccount() {
        if(accountRepository.findAll().size() == 0) {
            Account test = testUser(1);
            accountRepository.save(test);
        }
        assertTrue("First account cannot be added", accountRepository.findAll().size() != 0);   
    }
    
    @Test
    public void canMakeManyTestAccounts() {
        for(int id = 2; id < 6; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = testUser(id);
                accountRepository.save(test);
                }
        }
        assertTrue("Many test accounts cannot be added", accountRepository.findByProfilename("test5") != null);   
    }
    
    @Test
    public void statusOkforRoot() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    } 
 
    @Test
    public void statusOkforAccount() throws Exception {
        mockMvc.perform(get("/accounts")).andExpect(status().isOk());
    } 
    
   
    
}
