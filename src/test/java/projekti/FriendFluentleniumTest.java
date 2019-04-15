package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Java6Assertions.assertThat;
import org.junit.Before;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void init() {       
        for(int id = 301 ; id <= 305; id++ ) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = testUser(id);
                accountRepository.save(test);
            }
        }
    }    
    
    @Test
    public void cannotOpenKayttajaTest301twithoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat/test301");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    } 
    
    @Test
    public void canCreateFriendRequest() {
        Account fromAccount = accountRepository.findByProfilename("test301");
        Account byAccount = accountRepository.findByProfilename("test302");
        Friend testFriend = friendRepository.findByAskedbyAndAskedfrom(byAccount, fromAccount);
        if(testFriend != null) friendRepository.delete(testFriend);

        int friendsBefore = friendRepository.findAll().size();
        
        goTo("http://localhost:" + port + "/kayttajat/test301");
        enterDetailsAndSubmit("test302", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test301");
        find(By.name("Kaveriksi")).click();
        assertTrue(friendRepository.findAll().size() == friendsBefore + 1);
    }
    
    @Test
    public void canApproveFriendRequest() {
        Account fromAccount = accountRepository.findByProfilename("test301");
        Account byAccount = accountRepository.findByProfilename("test302");
        List <Friend> testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        goTo("http://localhost:" + port + "/kayttajat/test301");
        enterDetailsAndSubmit("test302", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test301");
        find(By.name("Kaveriksi")).submit();
        
        goTo("http://localhost:" + port + "/logout");
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("test301", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test301");
        find(By.name("submit")).click();
        
        assertTrue(friendRepository.findByAskedfromAndStatus(fromAccount, true).size() == 1);
    }
    
    @Test
    public void canRejectFriendRequest() {
        Account fromAccount = accountRepository.findByProfilename("test303");
        Account byAccount = accountRepository.findByProfilename("test304");
        List <Friend> testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        goTo("http://localhost:" + port + "/kayttajat/test303");
        enterDetailsAndSubmit("test304", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test303");
        find(By.name("Kaveriksi")).submit();
        
        goTo("http://localhost:" + port + "/logout");
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("test303", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test303");
        find(By.name("reject")).click();
        
        int friendsCount = friendRepository.findByAskedfromAndStatus(fromAccount, false).size() 
                + friendRepository.findByAskedfromAndStatus(fromAccount, true).size();
        
        assertTrue(friendsCount == 0);
    }
    
    @Test
    public void canSeeOneFriend() {
        Account fromAccount = accountRepository.findByProfilename("test305");
        Account byAccount = accountRepository.findByProfilename("test301");
        List <Friend> testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        Friend newFriends = new Friend();
        newFriends.setAskdate(LocalDate.now());
        newFriends.setAsktime(LocalTime.now());
        newFriends.setAskedby(byAccount);
        newFriends.setAskedfrom(fromAccount);
        newFriends.setStatus(true);
        friendRepository.save(newFriends);
        
        goTo("http://localhost:" + port + "/kayttajat/test305");
        enterDetailsAndSubmit("test305", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test305");
        assertThat(pageSource()).contains("301");
        friendRepository.delete(newFriends);
    }
    
    @Test
    public void canSeeOneReverseFriend() {
        Account fromAccount = accountRepository.findByProfilename("test301");
        Account byAccount = accountRepository.findByProfilename("test305");
        List <Friend> testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        Friend newFriends = new Friend();
        newFriends.setAskdate(LocalDate.now());
        newFriends.setAsktime(LocalTime.now());
        newFriends.setAskedby(byAccount);
        newFriends.setAskedfrom(fromAccount);
        newFriends.setStatus(true);
        friendRepository.save(newFriends);
        
        goTo("http://localhost:" + port + "/kayttajat/test305");
        enterDetailsAndSubmit("test305", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test305");
        assertThat(pageSource()).contains("301");
        friendRepository.delete(newFriends);
    }
    
    @Test
    public void canSeeTwoFriends() {
        Account fromAccount = accountRepository.findByProfilename("test304");
        Account byAccount1 = accountRepository.findByProfilename("test301");
        Account byAccount2 = accountRepository.findByProfilename("test302");
        List <Friend> testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        Friend newFriends = new Friend();
        newFriends.setAskdate(LocalDate.now());
        newFriends.setAsktime(LocalTime.now());
        newFriends.setAskedby(byAccount1);
        newFriends.setAskedfrom(fromAccount);
        newFriends.setStatus(true);
        friendRepository.save(newFriends);
        
        newFriends = new Friend();
        newFriends.setAskdate(LocalDate.now());
        newFriends.setAsktime(LocalTime.now());
        newFriends.setAskedby(byAccount2);
        newFriends.setAskedfrom(fromAccount);
        newFriends.setStatus(true);
        friendRepository.save(newFriends);
        
        goTo("http://localhost:" + port + "/kayttajat/test304");
        enterDetailsAndSubmit("test304", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test304");
        assertThat(pageSource()).contains("301").contains("302");
    }
    
    @Test
    public void cannotSeeAnyFriends() {
        Account fromAccount = accountRepository.findByProfilename("test305");
        Account byAccount = accountRepository.findByProfilename("test301");
        List <Friend> testFriends = friendRepository.findByAskedbyAndStatus(fromAccount, false);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        testFriends = friendRepository.findByAskedfromAndStatus(fromAccount, true);
        for(Friend testFriend : testFriends) {
            friendRepository.delete(testFriend);
        }
        
        goTo("http://localhost:" + port + "/kayttajat/test305");
        enterDetailsAndSubmit("test305", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test305");
        assertThat(pageSource()).doesNotContain("301");
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("FriendFluent Testaaja (test" + id +")");
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }   
}
