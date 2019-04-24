package projekti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
import org.springframework.test.context.ActiveProfiles;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WallFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    private WallRepository wallRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(600, 615);
        createFriends(600, 601);
        createFriends(602, 603);
        createFriends(604, 605);
        createFriends(611, 612);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Wall Fluentlenium Testaaja (test" + id + ")");
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
    public void canWriteToOwnWall() {
        Account account1 = accountRepository.findByProfilename("test606");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test606");
        enterDetailsAndSubmit("testi606", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test606");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        find(By.name("newWallMessage")).write("Testaan vaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    }
    
    @Test
    public void canWriteToFriendWall() {
        Account account1 = accountRepository.findByProfilename("test611");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test611");
        enterDetailsAndSubmit("testi612", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test611");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        find(By.name("newWallMessage")).write("Testaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    }
    
    @Test
    public void cannotWriteToNonFriendWall() {       
        goTo("http://localhost:" + port + "/kayttajat/test606");
        enterDetailsAndSubmit("testi605", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test606");
        assertThat(pageSource()).doesNotContain("newWallMessage");
        assertThat(pageSource()).doesNotContain("sendMessage");
    }
    
    @Test
    public void canWriteAndReadOwnWall() {
        Account account1 = accountRepository.findByProfilename("test607");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test607");
        enterDetailsAndSubmit("testi607", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test607");
        find(By.name("newWallMessage")).write("Testaan pikkusen");
        find(By.name("sendMessage")).click();
        
        goTo("http://localhost:" + port + "/kayttajat/test607");
        assertThat(pageSource()).contains("Testaan pikkusen");
        
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    }
    
    @Test
    public void canWriteAndReadManyMessagesToOwnWall() {
        Account account1 = accountRepository.findByProfilename("test608");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test608");
        enterDetailsAndSubmit("testi608", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test608");
        find(By.name("newWallMessage")).write("Testaan123");
        find(By.name("sendMessage")).click();
        
        goTo("http://localhost:" + port + "/kayttajat/test608");
        find(By.name("newWallMessage")).write("Testaan pikkusen");
        find(By.name("sendMessage")).click();
        
        goTo("http://localhost:" + port + "/kayttajat/test608");
        find(By.name("newWallMessage")).write("Testaan vielä");
        find(By.name("sendMessage")).click();
        
        goTo("http://localhost:" + port + "/kayttajat/test608");
        assertThat(pageSource()).contains("Testaan123");
        assertThat(pageSource()).contains("Testaan pikkusen");
        assertThat(pageSource()).contains("Testaan vielä");
        
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    }
    
    @Test
    public void canWriteAndReadFriendWall() {
        Account account1 = accountRepository.findByProfilename("test603");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test603");
        enterDetailsAndSubmit("testi602", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test603");
        find(By.name("newWallMessage")).write("Testaan vielä pikkusen");
        find(By.name("sendMessage")).click();
        
        goTo("http://localhost:" + port + "/kayttajat/test603");
        assertThat(pageSource()).contains("Testaan vielä pikkusen");
        
        List <Wall> tests = wallRepository.findByOwner(account1);
        for(Wall test : tests) {
            wallRepository.delete(test);
        }
    }
    
    @Test
    public void canReadNonFriendWall() {
        Wall wall = new Wall();
        wall.setTime(LocalDateTime.now());
        wall.setMessage("Pieni testi");
        Account owner = accountRepository.findByProfilename("test610");
        Account messager = accountRepository.findByProfilename("test609");
        wall.setMessager(messager);
        wall.setOwner(owner);
        wallRepository.save(wall);
        
        goTo("http://localhost:" + port + "/kayttajat/test610");
        enterDetailsAndSubmit("testi605", "test12345");
        assertThat(pageSource()).contains("Pieni testi");

        wallRepository.delete(wall);
    }
    
    @Test
    public void cannotOpenKayttajaTest601twithoutAccount() throws Exception {
        goTo("http://localhost:" + port + "/kayttajat/test611");
        assertThat(pageSource()).doesNotContain("NasuKirja");
    } 
    
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
     
}
