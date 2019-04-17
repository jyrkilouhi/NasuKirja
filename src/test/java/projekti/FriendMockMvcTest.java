package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private AccountService accountService;
    
    @Autowired
    private FriendsRepository friendRepository;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private int[] testUsersId = new int[10];
    
    @Before
    public void initTestUsers() {
        for(int id = 0; id <= 5; id++) {
            if(accountRepository.findByProfilename("test" + (id + 101)) == null) {
                Account test = createTestUser(id + 101);
                accountRepository.save(test);
            }
            testUsersId[id] = id+101;
        }
    }
    
    @After
    public void removeTestUsers() {
        for(int id = 0; id <= 5; id++) {
            Account test = accountRepository.findByProfilename("test" + (id + 101));
            accountRepository.delete(test);
        }
    }
    
    private Account createTestUser(int id) {
        Account test = new Account();
        test.setRealname("FriendMock Testaaja (test" + id + ")");
        test.setUsername("test" + id);
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
    public void canAddOneFriendRequest() {
        int friendsBefore = friendRepository.findAll().size();
        Friend test = createTestFriend(testUsersId[0] , testUsersId[1]);
        friendRepository.save(test);
        assertTrue("Friend request can be added", friendRepository.findAll().size() == friendsBefore + 1); 
        friendRepository.delete(test);
    }

    @Test
    public void canFindFriendRequestBetweenTwoAccounts() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[1]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[2]);
        Friend test = createTestFriend(testUsersId[1] , testUsersId[2]);
        friendRepository.save(test);
        assertTrue("Friend request can be found", friendRepository.findByAskedbyAndAskedfrom(account1, account2) != null);   
        friendRepository.delete(test);
    }
    
    @Test
    public void canFindFriendRequestByStatusAndAskedFrom() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[2]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[3]);
        Friend test = createTestFriend(testUsersId[2] , testUsersId[3]);
        friendRepository.save(test);
        assertTrue("Friend request can be found", friendRepository.findByAskedfromAndStatus(account2, false) != null);   
        friendRepository.delete(test);
    }
    
    @Test
    public void canFindFriendRequestByStatusAndAskedBy() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[1]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[3]);
        Friend test = createTestFriend(testUsersId[1] , testUsersId[3]);
        friendRepository.save(test);
        assertTrue("Friend request can be found", friendRepository.findByAskedbyAndStatus(account1, false) != null);   
        friendRepository.delete(test);
    }
    
    @Test
    public void requestTestForServiceMethod_isAskedToBeFriend() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[0]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[2]);
        Friend test = createTestFriend(testUsersId[0] , testUsersId[2]);
        friendRepository.save(test);
        assertTrue("Method isAskedToBeFriend founds friend request", friendService.isAskedToBeFriend(account1, account2));   
        friendRepository.delete(test);
    }
    
    @Test
    public void noRequestTestForServiceMethod_isAskedToBeFriend() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[0]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[3]);
        assertFalse("Method isAskedToBeFriend shoul not found request", friendService.isAskedToBeFriend(account1, account2));   
    }
    
    @Test
    public void areFriendsAlreadyTestForServiceMethod_isAskedToBeFriend() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[0]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[3]);
        Friend test = createTestFriend(testUsersId[0] , testUsersId[3]);
        test.setStatus(true);
        friendRepository.save(test);
        assertFalse("Method isAskedToBeFriend should found friend request when friends already", friendService.isAskedToBeFriend(account1, account2));   
        friendRepository.delete(test);
    }
    
    @Test
    public void areFriendsTestForServiceMethod_areFriends() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[0]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[4]);
        Friend test = createTestFriend(testUsersId[0] , testUsersId[4]);
        test.setStatus(true);
        friendRepository.save(test);
        assertTrue("Method areFriends found friends", friendService.areFriends(account1, account2));   
        friendRepository.delete(test);
    }
    
    @Test
    public void areNotFriendsTestForServiceMethod_areFriends() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[1]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[4]);
        Friend test = createTestFriend(testUsersId[0] , testUsersId[4]);
        assertFalse("Method areFriends should not found friends", friendService.areFriends(account1, account2));   
    }
    
    @Test
    public void onlyFriendRequstTestForServiceMethod_areFriends() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[1]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[4]);
        Friend test = createTestFriend(testUsersId[0] , testUsersId[4]);
        friendRepository.save(test);
        assertFalse("Method areFriends should not found friends", friendService.areFriends(account1, account2));   
        friendRepository.delete(test);
    }
    
    @Test
    public void reverseAreFriendsTestForServiceMethod_areFriends() {
        Account account1 = accountRepository.findByProfilename("test" + testUsersId[2]);
        Account account2 = accountRepository.findByProfilename("test" + testUsersId[4]);
        Friend test = createTestFriend(testUsersId[2] , testUsersId[4]);
        test.setStatus(true);
        friendRepository.save(test);
        assertTrue("Method areFriends found friends", friendService.areFriends(account2, account1));   
        friendRepository.delete(test);
    } 
    
}
