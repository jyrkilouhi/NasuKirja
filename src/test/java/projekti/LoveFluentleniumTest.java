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
public class LoveFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private LoveRepository loveRepository;

    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(1700, 1720);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Love Fluent (test" + id + ")");
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
    
    
    private Picture createPicture(String message, int ownerId) {
        Picture picture = new Picture();
        picture.setText(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        picture.setOwner(owner);
        picture.setContent("TestContent".getBytes());
        pictureRepository.save(picture);
        return picture;
    }
    
    private Wall createWall(String message, int ownerId, int messagerId) {
        Wall wall = new Wall();
        wall.setTime(LocalDateTime.now());
        wall.setMessage(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        Account messager = accountRepository.findByProfilename("test" + messagerId);
        wall.setMessager(messager);
        wall.setOwner(owner);
        wallRepository.save(wall);
        return wall;
    }
        
   
    @Test
    public void canLikeOwnWall() {
        Account account1 = accountRepository.findByProfilename("test1700");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        int likesBefore = loveRepository.findByLover(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test1700");
        enterDetailsAndSubmit("testi1700", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1700");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        assertThat(pageSource()).doesNotContain("LikeWall");
        find(By.name("newWallMessage")).write("Testaan vaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1700");
        assertThat(pageSource()).contains("LikeWall");
        assertThat(pageSource()).contains("likewall");
        find(By.name("likewall")).click();
        assertTrue(loveRepository.findByLover(account1).size() == likesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1700");
        assertThat(pageSource()).doesNotContain("LikeWall");
        assertThat(pageSource()).doesNotContain("likewall");
        assertThat(pageSource()).contains("1 tykkää tästä");        
    }
    
    @Test
    public void canLikeFriendWall() {
        Account account1 = accountRepository.findByProfilename("test1701");
        Account account2 = accountRepository.findByProfilename("test1702");
        createFriends(1701, 1702);
        
        int messagesBefore = wallRepository.findByOwner(account1).size();
        int likesBefore = loveRepository.findByLover(account2).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test1701");
        enterDetailsAndSubmit("testi1702", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1701");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        assertThat(pageSource()).doesNotContain("LikeWall");
        find(By.name("newWallMessage")).write("Testaan vaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1701");
        assertThat(pageSource()).contains("LikeWall");
        assertThat(pageSource()).contains("likewall");
        find(By.name("likewall")).click();
        assertTrue(loveRepository.findByLover(account2).size() == likesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1701");
        assertThat(pageSource()).doesNotContain("LikeWall");
        assertThat(pageSource()).doesNotContain("likewall");
        assertThat(pageSource()).contains("1 tykkää tästä");   
    }
    
    @Test
    public void cannotLikeNonFriendWallOrPicture() {   
        createPicture("TestiKuva" , 1719);
        createWall("TestiViesti", 1719, 1719);
        
        goTo("http://localhost:" + port + "/kayttajat/test1719");
        enterDetailsAndSubmit("testi1720", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1719");
        assertThat(pageSource()).doesNotContain("LikeWall");
        assertThat(pageSource()).doesNotContain("likewall");
        assertThat(pageSource()).doesNotContain("LikePicture");
        assertThat(pageSource()).doesNotContain("likepicture");
        assertThat(pageSource()).contains("TestiKuva");  
        assertThat(pageSource()).contains("TestiViesti");  
    }
       
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
     
}
