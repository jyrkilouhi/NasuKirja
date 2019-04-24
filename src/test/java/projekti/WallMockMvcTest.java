package projekti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.After;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WallMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private AccountRepository accountRepository;
   
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(500, 520);
        createFriends(500, 501);
        createFriends(502, 503);
        createFriends(504, 505);
        createFriends(506, 507);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Wall MockMvc Testaaja (test" + id + ")");
                test.setUsername("testi" + id);
                test.setProfilename("test" + id);
                test.setPassword(passwordEncoder.encode("test12345")); 
                accountRepository.save(test);
            }
        }
    }
    
    private void createFriends(int id1, int id2) {
        Friend test = new Friend();
        test.setAskdate(LocalDate.now());
        test.setAsktime(LocalTime.now());
        Account account1 = accountRepository.findByProfilename("test" + id1);
        test.setAskedby(account1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        test.setAskedfrom(account2);
        test.setStatus(true);
        if(friendRepository.findByAskedbyAndAskedfrom(account1, account2) != null) return;
        if(friendRepository.findByAskedbyAndAskedfrom(account2, account1) != null) return;
        friendRepository.save(test);
    }
    
    @Test
    @WithMockUser(username = "testi510")
    public void canWriteToOwnWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test510");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test510")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test510").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test510"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message written to repository" , tests.size() == messagesBefore + 1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi501")
    public void canWriteToFriendWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test500");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test500")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test500").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test500"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message written to repository" , tests.size() == messagesBefore + 1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi511")
    public void cannotWriteToNonFriendWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test512");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test512")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test512").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test512"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message not written to repository" , tests.size() == messagesBefore);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi513")
    public void canSeeMessageOnOwnWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test513").param("newWallMessage", "testiviesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test513"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test513")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible at wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test513");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi514")
    public void canSeeManyMessageOnOwnWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test514").param("newWallMessage", "testiviesti"))
              .andExpect(redirectedUrl("/kayttajat/test514"));
        mockMvc.perform(post("/kayttajat/wall/test514").param("newWallMessage", "testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test514"));
        mockMvc.perform(post("/kayttajat/wall/test514").param("newWallMessage", "testiviesti vain"))
              .andExpect(redirectedUrl("/kayttajat/test514"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test514")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Messages visible at wall ", messages.size() == 3); 

        Account account1 = accountRepository.findByProfilename("test514");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 

    @Test
    @WithMockUser(username = "testi502")
    public void canSeeMessageOnFriendWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test503").param("newWallMessage", "testiviesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test503"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test503")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible on wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test503");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi515")
    public void canSeeMessageOnNonFriendWall() throws Exception {
        Wall wall = new Wall();
        wall.setTime(LocalDateTime.now());
        wall.setMessage("Pieni testti");
        Account owner = accountRepository.findByProfilename("test516");
        Account messager = accountRepository.findByProfilename("test517");
        wall.setMessager(messager);
        wall.setOwner(owner);
        wallRepository.save(wall);
        
        MvcResult result = mockMvc.perform(get("/kayttajat/test516")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible on wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test516");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi517")
    public void cannotWriteEmptyMessageToWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test517");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test517")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test517").param("newWallMessage", " "))
              .andExpect(redirectedUrl("/kayttajat/test517"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message not written to repository" , tests.size() == messagesBefore);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
           
    @Test
    @WithMockUser(username = "testi509")
    public void cannotWriteWallOfNonUser() throws Exception {
        int messagesBefore = wallRepository.findAll().size();
        mockMvc.perform(get("/kayttajat/test5555")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test5555").param("newWallMessage", "Testi"))
              .andExpect(redirectedUrl("/kayttajat/test5555"));
        List <Wall> tests = wallRepository.findAll();
        assertTrue("Wall message not written to repository" , tests.size() == messagesBefore);
    } 
  
    
    @Test
    public void anonymousCannotWriteToWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test508").param("newWallMessage","ERRORCODE12345"))
              .andExpect(redirectedUrl("http://localhost/login"));
        
        Account account1 = accountRepository.findByProfilename("test508");
        List <Wall> requests = wallRepository.findByOwner(account1);
        assertTrue("Anonymous Not able to write to wall" , requests.size() == 0);
        for(Wall wall: requests) {
            wallRepository.delete(wall);
        }
    }
    
    @Test
    public void anonymousCannotReadWall() throws Exception {
        mockMvc.perform(get("/kayttajat/wall/test507"))
              .andExpect(redirectedUrl("http://localhost/login"));
    }
    
}
