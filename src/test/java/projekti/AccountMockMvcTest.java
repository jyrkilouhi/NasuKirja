package projekti;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
      
    @Before
    public void init() {
        if(accountRepository.findByProfilename("test1") == null) {
            Account test = createTestUser(1);
            accountRepository.save(test);
        }
    }
    
    private Account createTestUser(int id) {
        Account test = new Account();
        test.setRealname("Account MockMvc Testaaja (test" + id + ")");
        test.setUsername("testi" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
   
    @Test
    public void statusOkforAccountPage() throws Exception {
        mockMvc.perform(get("/accounts")).andExpect(status().isOk());
    } 

    @Test
    @WithMockUser(username = "testi1")
    public void statusOkforOmasivu() throws Exception {
        mockMvc.perform(get("/omasivu")).andExpect(redirectedUrl("/kayttajat/test1")).andExpect(status().isFound());
    } 
    
    @Test
    @WithMockUser(username = "testi1")
    public void statusOkforKayttajaTest1Page() throws Exception {
        mockMvc.perform(get("/kayttajat/test1")).andExpect(status().isOk());
    } 
    
    @Test
    @WithMockUser(username = "testi2")
    public void statusOkforKayttajaTest1PageWithOtherAccount() throws Exception {
        mockMvc.perform(get("/kayttajat/test1")).andExpect(status().isOk());
    } 
    
    @Test
    @WithMockUser(username = "testi1")
    public void getCorrectModelFromOwnPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test1")).andReturn();
        String modelText = result.getModelAndView().getModel().get("IsMyPage").toString();
        assertTrue("Return correct model for ownPage", modelText != null); 
    } 
    
    @Test
    @WithMockUser(username = "testi2")
    public void modelForKayttajaTest1PageWithOtherAccount() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test1")).andReturn();
        List<String> models= (List)result.getModelAndView().getModel().get("IsMyPage");
        assertTrue("Model should not include IsMyPage" , models == null);
    } 
    
}
