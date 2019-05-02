package projekti;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private int idForNextTestUser = 202;
    
    @Before
    public void init() {            
        if(accountRepository.findByProfilename("test201") == null) {
            Account test = testUser(201);
            accountRepository.save(test);
        }
        while(accountRepository.findByProfilename("test" + idForNextTestUser) != null) {
            idForNextTestUser++;
        }
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
    public void cannotOpenKayttajaTest201withoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat/test201");
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
        enterDetailsAndSubmit("testi201", "v123");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    }

    @Test
    public void authSuccessfulOnCorrectPasswordToKayttajat() {
        goTo("http://localhost:" + port + "/kayttajat");
        enterDetailsAndSubmit("testi201", "test12345");
        assertThat(pageSource()).contains("NasuKirja");
    }
    
    @Test
    public void authSuccessfulOnCorrectPasswordToKayttajaTest201() {
        goTo("http://localhost:" + port + "/kayttajat/test201");
        enterDetailsAndSubmit("testi201", "test12345");
        assertThat(pageSource()).contains("NasuKirja");
    }
    
    @Test
    public void canCreateNewAccount() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore + 1);
        testAccount =accountRepository.findByProfilename("test" + idForNextTestUser );
        accountRepository.delete(testAccount);
    }
    
    @Test
    public void cannotCreateAccountWithSameProfilename() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        testAccount.setProfilename("test201");
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    @Test
    public void cannotCreateAccountWithoutProfilename() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        testAccount.setProfilename("");
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    @Test
    public void cannotCreateAccountWithWrongCharacterAtProfilename() {
        assertTrue(testAccountWithWrongProfilename(3001, "?????"));
        assertTrue(testAccountWithWrongProfilename(3002, "!!!!!"));
        assertTrue(testAccountWithWrongProfilename(3003, "#####"));
        assertTrue(testAccountWithWrongProfilename(3004, "€€€€€"));
        assertTrue(testAccountWithWrongProfilename(3005, "%%%%%%"));
        assertTrue(testAccountWithWrongProfilename(3006, "&&&&&&"));
        assertTrue(testAccountWithWrongProfilename(3007, "//////"));
        assertTrue(testAccountWithWrongProfilename(3008, "(((((("));
        assertTrue(testAccountWithWrongProfilename(3009, "))))))"));
        assertTrue(testAccountWithWrongProfilename(3011, "======"));
        assertTrue(testAccountWithWrongProfilename(3011, "......"));
        assertTrue(testAccountWithWrongProfilename(3012, ",,,,,,"));
        assertTrue(testAccountWithWrongProfilename(3013, ":::::::"));
        assertTrue(testAccountWithWrongProfilename(3014, "******"));
        assertTrue(testAccountWithWrongProfilename(3015, "aa aaa"));
        assertTrue(testAccountWithWrongProfilename(3016, "§§§§§§"));        
        assertTrue(testAccountWithWrongProfilename(3017, "\\\\\\"));  
        assertTrue(testAccountWithWrongProfilename(3018, "{{{{{{"));  
        assertTrue(testAccountWithWrongProfilename(3019, "}}}}}}"));  
        assertFalse(testAccountWithWrongProfilename(3020, "abcde"));       
    }
    

    @Test
    public void cannotCreateAccountWithSameUsername() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        testAccount.setUsername("testi201");
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    @Test
    public void cannotCreateAccountWithoutUsername() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        testAccount.setUsername("");
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    @Test
    public void cannotCreateAccountWithoutRealname() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        testAccount.setRealname("");
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    @Test
    public void cannotCreateAccountWithShortPassword() {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(idForNextTestUser);
        enterAccountDetailsAndSubmit(testAccount,"te");
        assertTrue(accountRepository.findAll().size() == accountsBefore );
    }
    
    
    private void enterAccountDetailsAndSubmit(Account newAccount, String password) {
        find(By.name("realname")).fill().with(newAccount.getRealname());
        find(By.name("profilename")).fill().with(newAccount.getProfilename());
        find(By.name("username")).fill().with(newAccount.getUsername());
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
    
    private Account testUser(int id) {
        Account test = new Account();
        test.setRealname("AccountFluent Testaaja (test" + id +")");
        test.setUsername("testi" + id);
        test.setProfilename("test" + id);
        test.setPassword(passwordEncoder.encode("test12345")); 
        
        return test;
    }
    

    public boolean testAccountWithWrongProfilename(int id, String profilename) {
        int accountsBefore = accountRepository.findAll().size();
        goTo("http://localhost:" + port + "/accounts");
        Account testAccount = testUser(id);
        testAccount.setProfilename(profilename);
        enterAccountDetailsAndSubmit(testAccount, "test12345");
        return (accountRepository.findAll().size() == accountsBefore );
    }
    
}
