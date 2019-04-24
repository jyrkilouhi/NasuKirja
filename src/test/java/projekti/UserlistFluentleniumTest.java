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
import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserlistFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(700, 705);
    }

    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Userlist Fluentlenium Testaaja (test" + id + ")");
                test.setUsername("testi" + id);
                test.setProfilename("test" + id);
                test.setPassword(passwordEncoder.encode("test12345")); 
                accountRepository.save(test);
            }
        }
    }    
    
    @Test
    public void canFoundAllAccounts() {        
        goTo("http://localhost:" + port + "/kayttajat/test701");
        enterDetailsAndSubmit("testi701", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test701");
        assertThat(pageSource()).contains("findname");
        find(By.name("findname")).write("").submit();
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test700)");
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test701)");
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test702)");
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test703)");
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test704)");
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test705)");
    }
    
    @Test
    public void canFoundOnlyOneAccount() {        
        goTo("http://localhost:" + port + "/kayttajat/test701");
        enterDetailsAndSubmit("testi701", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test701");
        assertThat(pageSource()).contains("findname");
        find(By.name("findname")).write("test700").submit();
        assertThat(pageSource()).contains("Userlist Fluentlenium Testaaja (test700)");
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test701)");
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test702)");
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test703)");
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test704)");
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test705)");
    }    
    
    @Test
    public void userGetsErrorMessageWhenNoAccountsFound() {        
        goTo("http://localhost:" + port + "/kayttajat/test701");
        enterDetailsAndSubmit("testi701", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test701");
        assertThat(pageSource()).contains("findname");
        find(By.name("findname")).write("test700jhfgjsdhjashdfjbsj").submit();
        assertThat(pageSource()).doesNotContain("Userlist Fluentlenium Testaaja (test700)");
        assertThat(pageSource()).contains("ei löydy yhtään käyttäjää");
    } 
    
    @Test
    public void cannotOpenUserlistWithoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat/");
        assertThat(pageSource()).doesNotContain("NasuKirja");
        goTo("http://localhost:" + port + "/");
        assertThat(pageSource()).doesNotContain("findname");
    }     
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }    
}
