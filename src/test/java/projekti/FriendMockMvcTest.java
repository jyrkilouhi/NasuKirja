package projekti;

import java.time.LocalDate;
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
public class FriendMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    private AccountRepository accountRepository;
   
    @Autowired
    private FriendsRepository friendRepository;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private int[] testUsersId = new int[15];
    
    @Before
    public void initTestUsers() {
        for(int id = 0; id <= 10; id++) {
            if(accountRepository.findByProfilename("test" + (id + 101)) == null) {
                Account test = createTestUser(id + 101);
                accountRepository.save(test);
            }
            testUsersId[id] = id+101;
        }
    }
    
    @After
    public void removeTestUsers() {
        for(int id = 0; id <= 10; id++) {
            Account test = accountRepository.findByProfilename("test" + (id + 101));
            accountRepository.delete(test);
        }
    }
    
        private Account createTestUser(int id) {
        Account test = new Account();
        test.setRealname("Friend MockMvc Testaaja (test" + id + ")");
        test.setUsername("testi" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
    
    private Friend createTestFriend(int id1, int id2) {
        Friend test = new Friend();
        test.setAskdate(LocalDate.now());
        test.setAsktime(LocalTime.now());
        Account account1 = accountRepository.findByProfilename("test" + id1);
        test.setAskedby(account1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        test.setAskedfrom(account2);
        test.setStatus(false);
        
        return test;
    }
    
    @Test
    @WithMockUser(username = "testi101")
    public void canSendFriendRequestToOtherAccount() throws Exception {
        mockMvc.perform(get("/kayttajat/test102")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/request/test102"))
              .andExpect(redirectedUrl("/kayttajat/test102"));
        Account account1 = accountRepository.findByProfilename("test101");
        Account account2 = accountRepository.findByProfilename("test102");
        Friend test = friendRepository.findByAskedbyAndAskedfrom(account1, account2);
        assertTrue("Friend request visible at repository" , test != null);
        friendRepository.delete(test);
    } 
    
    @Test
    @WithMockUser(username = "testi102")
    public void canSeeFriendRequestFromOtherAccount() throws Exception {
        Friend test = createTestFriend(101, 102);
        friendRepository.save(test);
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Account> requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("Friendrequest visible at user page ", requests.size() == 1); 
        friendRepository.delete(test);
    } 
    
    @Test
    @WithMockUser(username = "testi102")
    public void cannotSeeFriendRequestWhenNotInPlace() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Account> requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("Friendrequest not visible at user page ", requests.size() == 0); 
    } 
    
    @Test
    @WithMockUser(username = "testi101")
    public void cannotSendFriendRequestToMyself() throws Exception {
        mockMvc.perform(get("/kayttajat/test101")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/request/test101"))
              .andExpect(redirectedUrl("/kayttajat/test101"));
        Account account1 = accountRepository.findByProfilename("test101");
        List <Friend> requests = friendRepository.findByAskedfromAndStatus(account1, false);
        assertTrue("Not able to send friend request to myself" , requests.size() == 0);
        for(Friend friend: requests) {
            friendRepository.delete(friend);
        }
    }
        
    @Test
    @WithMockUser(username = "testi101")
    public void cannotSendFriendRequestToOtherAccountTwoTimes() throws Exception {
        mockMvc.perform(get("/kayttajat/test102")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/request/test102"))
              .andExpect(redirectedUrl("/kayttajat/test102"));
        Account account2 = accountRepository.findByProfilename("test102");
        List <Friend> requests = friendRepository.findByAskedfromAndStatus(account2, false);        
        assertTrue("Friend request visible at repository" , requests.size() == 1);
        mockMvc.perform(get("/kayttajat/test102")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/request/test102"))
              .andExpect(redirectedUrl("/kayttajat/test102"));
        requests = friendRepository.findByAskedfromAndStatus(account2, false);        
        assertTrue("only one Friend request visible at repository" , requests.size() == 1) ;       
        for(Friend friend: requests) {
            friendRepository.delete(friend);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi101")
    public void cannotSendFriendRequestToAccountFriendAlready() throws Exception {
        Friend test = createTestFriend(101, 102);
        test.setStatus(true);
        friendRepository.save(test);
        mockMvc.perform(get("/kayttajat/test102")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/request/test102"))
              .andExpect(redirectedUrl("/kayttajat/test102"));
        Account account2 = accountRepository.findByProfilename("test102");
        List<Friend> requests = friendRepository.findByAskedfromAndStatus(account2, false);        
        assertTrue("Should not save new request if already friends" , requests.size() == 0) ;       
        for(Friend friend: requests) {
            friendRepository.delete(friend);
        }
        friendRepository.delete(test);
    } 
    
    @Test
    @WithMockUser(username = "testi102")
    public void canApproveFriendRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Friend> friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("Friends not visible at user page yet ", friends.size() == 0);     
        
        Friend test = createTestFriend(103, 102);
        friendRepository.save(test);

        mockMvc.perform(post("/kayttajat/approve/test103"))
              .andExpect(redirectedUrl("/kayttajat/test102"));
        
        Account account1 = accountRepository.findByProfilename("test102");
        friends = friendRepository.findByAskedfromAndStatus(account1, true); 
        assertTrue("Should have one friend" , friends.size() == 1) ;   
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("One friend visible at user page", friends.size() == 1);  
        
        friends = friendRepository.findByAskedfromAndStatus(account1, true);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
        friends = friendRepository.findByAskedfromAndStatus(account1, false);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
    } 

    @Test
    @WithMockUser(username = "testi102")
    public void canRejectFriendRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Friend> friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("Friends not visible at user page yet ", friends.size() == 0);     
        
        Friend test = createTestFriend(103, 102);
        friendRepository.save(test);
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Account> requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("Friendrequest visible at user page ", requests.size() == 1); 

        mockMvc.perform(post("/kayttajat/reject/test103")).andExpect(redirectedUrl("/kayttajat/test102"));
        
        Account account1 = accountRepository.findByProfilename("test102");
        friends = friendRepository.findByAskedfromAndStatus(account1, true); 
        assertTrue("Should have no friends" , friends.size() == 0) ;   
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("No friend visible at user page", friends.size() == 0); 
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("Friendrequest not anymore visible at user page ", requests.size() == 0); 
        
        friends = friendRepository.findByAskedfromAndStatus(account1, true);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
        friends = friendRepository.findByAskedfromAndStatus(account1, false);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
    }
    
    @Test
    @WithMockUser(username = "testi102")
    public void cannotApproveNotExistingFriendRequest() throws Exception {
      
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Account> requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("No Friendrequest visible at user page ", requests.size() == 0); 

        mockMvc.perform(post("/kayttajat/approve/test103")).andExpect(redirectedUrl("/kayttajat/test102"));
        
        Account account1 = accountRepository.findByProfilename("test102");
        List<Friend> friends = friendRepository.findByAskedfromAndStatus(account1, true); 
        assertTrue("Should have no friends" , friends.size() == 0) ;   
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("No friend visible at user page", friends.size() == 0); 
              
        friends = friendRepository.findByAskedfromAndStatus(account1, true);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
        friends = friendRepository.findByAskedfromAndStatus(account1, false);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
    }
    
    @Test
    @WithMockUser(username = "testi102")
    public void cannotApproveFriendRequestIfFriendsAlready() throws Exception {

        Friend test = createTestFriend(103, 102);
        test.setStatus(true);
        friendRepository.save(test);
        MvcResult result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        List<Account> requests = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("Friend visible at user page ", requests.size() == 1);
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        requests = (List)result.getModelAndView().getModel().get("FriendRequests");
        assertTrue("No Friendrequest visible at user page ", requests.size() == 0); 

        mockMvc.perform(post("/kayttajat/approve/test103")).andExpect(redirectedUrl("/kayttajat/test102"));
        
        Account account1 = accountRepository.findByProfilename("test102");
        List<Friend> friends = friendRepository.findByAskedfromAndStatus(account1, true); 
        assertTrue("Should have no friends" , friends.size() == 1) ;   
        
        result = mockMvc.perform(get("/kayttajat/test102")).andReturn();
        friends = (List)result.getModelAndView().getModel().get("Friends");
        assertTrue("Just one friend visible at user page", friends.size() == 1); 
              
        friends = friendRepository.findByAskedfromAndStatus(account1, true);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
        friends = friendRepository.findByAskedfromAndStatus(account1, false);
        for(Friend friend: friends) {
            friendRepository.delete(friend);
        }
    }
    
    @Test
    public void anonymousCannotSendFriendRequest() throws Exception {
        mockMvc.perform(post("/kayttajat/request/test101"))
              .andExpect(redirectedUrl("http://localhost/login"));
        
        Account account1 = accountRepository.findByProfilename("test101");
        List <Friend> requests = friendRepository.findByAskedfromAndStatus(account1, false);
        assertTrue("Anonymous Not able to send friend request" , requests.size() == 0);
        for(Friend friend: requests) {
            friendRepository.delete(friend);
        }
    }

    @Test
    public void anonymousCannotApproveFriendRequest() throws Exception {
        mockMvc.perform(post("/kayttajat/approve/test101"))
              .andExpect(redirectedUrl("http://localhost/login"));
        
        Account account1 = accountRepository.findByProfilename("test101");
        List <Friend> requests = friendRepository.findByAskedfromAndStatus(account1, true);
        assertTrue("Anonymous Not able to approve friend request" , requests.size() == 0);
        for(Friend friend: requests) {
            friendRepository.delete(friend);
        }
    }
    
    @Test
    public void anonymousCannotRejectFriendRequest() throws Exception {
        mockMvc.perform(post("/kayttajat/reject/test101"))
              .andExpect(redirectedUrl("http://localhost/login"));
        
    }
    
}
