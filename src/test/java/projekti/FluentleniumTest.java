package projekti;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Java6Assertions.assertThat;
import org.junit.Before;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void init() {            
        if(accountRepository.findByProfilename("test100") == null) {
            Account test = testUser(100);
            accountRepository.save(test);
        }
    }
    
    @Test
    public void anyoneCanOpenRoot() throws Exception {
        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).contains("NasuKirja");
    } 

    @Test
    public void anyoneCanOpenAccounts() throws Exception {
        goTo("http://localhost:" + port + "/accounts");
        assertThat(pageSource()).contains("NasuKirja");
    } 
    
    @Test
    public void cannotOpenKayttajatwithoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    } 

    @Test
    public void cannotOpenKayttajaTest100withoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat/test100");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    } 
    
    @Test
    public void shouldSeeLoginPageOnAccessingKayttajat() {
        goTo("http://localhost:" + port + "/kayttajat");
        assertThat(find(By.name("username"))).isNotNull();
        assertThat(find(By.name("password"))).isNotNull();
    }
    
    @Test
    public void noAuthOnWrongPassword() {
        goTo("http://localhost:" + port + "/kayttajat");
        enterDetailsAndSubmit("test100", "v123");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    }

    @Test
    public void authSuccessfulOnCorrectPasswordToKayttajat() {
        goTo("http://localhost:" + port + "/kayttajat");
        enterDetailsAndSubmit("test100", "test12345");
        assertThat(pageSource()).contains("NasuKirja");
    }
    
    @Test
    public void authSuccessfulOnCorrectPasswordToKayttajaTest100() {
        goTo("http://localhost:" + port + "/kayttajat/test100");
        enterDetailsAndSubmit("test100", "test12345");
        assertThat(pageSource()).contains("NasuKirja");
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("Testaaja" + id);
        test.setUsername("test" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
    
}
