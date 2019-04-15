package projekti;

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
        int friendsBefore = friendRepository.findAll().size();
        goTo("http://localhost:" + port + "/kayttajat/test301");
        enterDetailsAndSubmit("test302", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test301");
        find(By.name("Kaveriksi")).submit();
        assertTrue(friendRepository.findAll().size() == friendsBefore + 1);
        Account fromAccount = accountRepository.findByProfilename("test301");
        Account byAccount = accountRepository.findByProfilename("test302");
        Friend testFriend = friendRepository.findByAskedbyAndAskedfrom(byAccount, fromAccount);
        friendRepository.delete(testFriend);
    }
    
 
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("FriendFluent Testaaja (" + id +")");
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }   
}
