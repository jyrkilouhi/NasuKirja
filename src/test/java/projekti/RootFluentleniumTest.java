package projekti;

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
public class RootFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void init() {            
        if(accountRepository.findByProfilename("test200") == null) {
            Account test = testUser(200);
            accountRepository.save(test);
        }
    }
    
    @Test
    public void anyoneCanOpenRoot() throws Exception {
        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("NasuKirja");
    }   
    
    @Test
    public void authorizedAccountCanOpenRoot() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("test200", "test12345");
        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("NasuKirja");
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("Root Testaaja (test" + id +")");
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
    
}
