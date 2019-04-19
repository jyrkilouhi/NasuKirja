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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest

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
        createTestUsers(500, 505);
        createFriends(500, 501);
        createFriends(501, 502);
    }
    
    @After
    public void removeTestUsersAndFriends() {
        deleteFriends(500, 501);
        deleteFriends(501, 502);
        for(int id = 500; id <= 505; id++) {
            Account test = accountRepository.findByProfilename("test" + id );
            accountRepository.delete(test);
        }
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
        friendRepository.save(test);
    }
    
        
    private void deleteFriends(int id1, int id2) {
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        Friend test = friendRepository.findByAskedbyAndAskedfrom(account1, account2);
        if(test != null) friendRepository.delete(test);
    }
    
    @Test
    @WithMockUser(username = "testi501")
    public void canWriteToOwnWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test501");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test501")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test501").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test501"));
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
    @WithMockUser(username = "testi505")
    public void cannotWriteToNonFriendWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test500");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test500")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test500").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test500"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message not written to repository" , tests.size() == messagesBefore);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi501")
    public void canSeeMessageOnOwnWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test501").param("newWallMessage", "testiviesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test501"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test501")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible at wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test501");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi501")
    public void canSeeManyMessageOnOwnWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test501").param("newWallMessage", "testiviesti"))
              .andExpect(redirectedUrl("/kayttajat/test501"));
        mockMvc.perform(post("/kayttajat/wall/test501").param("newWallMessage", "testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test501"));
        mockMvc.perform(post("/kayttajat/wall/test501").param("newWallMessage", "testiviesti vain"))
              .andExpect(redirectedUrl("/kayttajat/test501"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test501")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Messages visible at wall ", messages.size() == 3); 

        Account account1 = accountRepository.findByProfilename("test501");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 

    @Test
    @WithMockUser(username = "testi501")
    public void canSeeMessageOnFriendWall() throws Exception {
        mockMvc.perform(post("/kayttajat/wall/test502").param("newWallMessage", "testiviesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test502"));
        MvcResult result = mockMvc.perform(get("/kayttajat/test502")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible on wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test502");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi505")
    public void canSeeMessageOnNonFriendWall() throws Exception {
        Wall wall = new Wall();
        wall.setTime(LocalDateTime.now());
        wall.setMessage("Pieni testti");
        Account owner = accountRepository.findByProfilename("test502");
        Account messager = accountRepository.findByProfilename("test501");
        wall.setMessager(messager);
        wall.setOwner(owner);
        wallRepository.save(wall);
        
        MvcResult result = mockMvc.perform(get("/kayttajat/test502")).andReturn();
        List<Wall> messages = (List)result.getModelAndView().getModel().get("WallMessages");
        assertTrue("Message visible on wall ", messages.size() == 1); 

        Account account1 = accountRepository.findByProfilename("test502");
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi501")
    public void cannotWriteEmptyMessageToWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test500");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test500")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test500").param("newWallMessage", " "))
              .andExpect(redirectedUrl("/kayttajat/test500"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message not written to repository" , tests.size() == messagesBefore);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    } 
           
    @Test
    @WithMockUser(username = "testi501")
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
        mockMvc.perform(post("/kayttajat/wall/test505").param("newWallMessage","ERRORCODE12345"))
              .andExpect(redirectedUrl("http://localhost/login"));
        
        Account account1 = accountRepository.findByProfilename("test505");
        List <Wall> requests = wallRepository.findByOwner(account1);
        assertTrue("Anonymous Not able to write to wall" , requests.size() == 0);
        for(Wall wall: requests) {
            wallRepository.delete(wall);
        }
    }
    
    @Test
    public void anonymousCannotReadWall() throws Exception {
        mockMvc.perform(get("/kayttajat/wall/test505"))
              .andExpect(redirectedUrl("http://localhost/login"));
    }
    
}
