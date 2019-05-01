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
public class CommentFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
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
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(1600, 1620);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Coment Fluentlium Testaaja (test" + id + ")");
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
    public void canCommentOwnWall() {
        Account account1 = accountRepository.findByProfilename("test1600");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test1600");
        enterDetailsAndSubmit("testi1600", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1600");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        assertThat(pageSource()).doesNotContain("LikeWall");
        find(By.name("newWallMessage")).write("Testaan vaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1600");
        assertThat(pageSource()).contains("newWallComment");
        find(By.name("newWallComment")).write("COMMENT12345").submit();
        assertTrue(commentRepository.findByCommenter(account1).size() == commentsBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1600");
        assertThat(pageSource()).contains("newWallComment");        
        assertThat(pageSource()).contains("COMMENT12345");   
    }
    
    @Test
    public void canCommmentFriendWall() {
        Account account1 = accountRepository.findByProfilename("test1601");
        Account account2 = accountRepository.findByProfilename("test1602");
        createFriends(1601, 1602);
        
        int messagesBefore = wallRepository.findByOwner(account1).size();
        int commentsBefore = commentRepository.findByCommenter(account2).size();
        
        goTo("http://localhost:" + port + "/kayttajat/test1601");
        enterDetailsAndSubmit("testi1602", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1601");
        assertThat(pageSource()).contains("newWallMessage");
        assertThat(pageSource()).contains("sendMessage");
        assertThat(pageSource()).doesNotContain("LikeWall");
        find(By.name("newWallMessage")).write("Testaan vaan pikkusen.");
        find(By.name("sendMessage")).click();
        assertTrue(wallRepository.findByOwner(account1).size() == messagesBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1601");
        assertThat(pageSource()).contains("newWallComment");
        find(By.name("newWallComment")).write("COMMENTXXX").submit();
        assertTrue(commentRepository.findByCommenter(account2).size() == commentsBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1601");
        assertThat(pageSource()).contains("newWallComment");        
        assertThat(pageSource()).contains("COMMENTXXX"); 
           
    }
    
    @Test
    public void canCommentOwnPicture() {
        Account account1 = accountRepository.findByProfilename("test1605");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        createPicture("TestiKuva05", 1605);        

        goTo("http://localhost:" + port + "/kayttajat/test1605");
        enterDetailsAndSubmit("testi1605", "test12345");        
        goTo("http://localhost:" + port + "/kayttajat/test1605");
        assertThat(pageSource()).contains("newPictureComment");
        find(By.name("newPictureComment")).write("COMMENT05").submit();
        assertTrue(commentRepository.findByCommenter(account1).size() == commentsBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1605");
        assertThat(pageSource()).contains("newPictureComment");        
        assertThat(pageSource()).contains("COMMENT05");   
    }

    @Test
    public void canCommentFriendPicture() {
        Account account1 = accountRepository.findByProfilename("test1611");
        Account account2 = accountRepository.findByProfilename("test1612");
        createFriends(1611, 1612);
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        createPicture("TestiKuva05", 1612);        

        goTo("http://localhost:" + port + "/kayttajat/test1612");
        enterDetailsAndSubmit("testi1611", "test12345");        
        goTo("http://localhost:" + port + "/kayttajat/test1612");
        assertThat(pageSource()).contains("newPictureComment");
        find(By.name("newPictureComment")).write("COMMENT05").submit();
        assertTrue(commentRepository.findByCommenter(account1).size() == commentsBefore + 1);
        
        goTo("http://localhost:" + port + "/kayttajat/test1612");
        assertThat(pageSource()).contains("newPictureComment");        
        assertThat(pageSource()).contains("COMMENT05");   
    }
    
    @Test
    public void cannotCommentNonFriendWallOrPicture() {       
        createPicture("TestiKuvaYY" , 1619);
        createWall("TestiViestiYY", 1619, 1619);
        
        goTo("http://localhost:" + port + "/kayttajat/test1619");
        enterDetailsAndSubmit("testi1620", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1619");
        assertThat(pageSource()).doesNotContain("newWallComment");
        assertThat(pageSource()).doesNotContain("newPictureComment");
        assertThat(pageSource()).contains("TestiKuvaYY");  
        assertThat(pageSource()).contains("TestiViestiYY");  
    }
    

        
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
     
}
