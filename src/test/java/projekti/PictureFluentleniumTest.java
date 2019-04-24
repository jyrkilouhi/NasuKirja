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
public class PictureFluentleniumTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @org.springframework.boot.web.server.LocalServerPort
    private Integer port;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(1000, 1010);

    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Picture Fluentlenium Testaaja (test" + id + ")");
                test.setUsername("testi" + id);
                test.setProfilename("test" + id);
                test.setPassword(passwordEncoder.encode("test12345")); 
                accountRepository.save(test);
            }
        }
    }
    
    private Picture createPicture(String message, int ownerId) {
        Picture picture = new Picture();
        picture.setText(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        picture.setOwner(owner);
        picture.setContent("TestContent".getBytes());;
        return picture;
    }
    
    @Test
    public void sendPictureBoxVisibleAtOwnPage() {
        
        goTo("http://localhost:" + port + "/kayttajat/test1000");
        enterDetailsAndSubmit("testi1000", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1000");
        assertThat(pageSource()).contains("file");
        assertThat(pageSource()).contains("newPictureMessage");
        assertThat(pageSource()).contains("sendPicture");
    }
    
    @Test
    public void sendPictureBoxNotVisibleAtOtherUserPage() {
        
        goTo("http://localhost:" + port + "/kayttajat/test1001");
        enterDetailsAndSubmit("testi1000", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1001");
        assertThat(pageSource()).doesNotContain("file");
        assertThat(pageSource()).doesNotContain("newPictureMessage");
        assertThat(pageSource()).doesNotContain("sendPicture");
    }
    
    @Test
    public void SeePictureAtOwnPage() {
        Picture picture = createPicture("testiKuva", 1002);
        pictureRepository.save(picture);
        
        goTo("http://localhost:" + port + "/kayttajat/test1002");
        enterDetailsAndSubmit("testi1002", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1002");
        assertThat(pageSource()).contains("testiKuva");
        
        pictureRepository.delete(picture);
    }
    
    @Test
    public void SeePictureAtOtherUsersPage() {
        Picture picture = createPicture("testiKuva", 1003);
        pictureRepository.save(picture);
        
        goTo("http://localhost:" + port + "/kayttajat/test1003");
        enterDetailsAndSubmit("testi1004", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1003");
        assertThat(pageSource()).contains("testiKuva");
        
        pictureRepository.delete(picture);
    }
    
    @Test
    public void canDeletePicture() {
        Picture picture = createPicture("testiKuva", 1005);
        pictureRepository.save(picture);
        
        goTo("http://localhost:" + port + "/kayttajat/test1005");
        enterDetailsAndSubmit("testi1005", "test12345");
        goTo("http://localhost:" + port + "/kayttajat/test1005");
        assertThat(pageSource()).contains("testiKuva");
        goTo("http://localhost:" + port + "/kayttajat/test1005");
        find(By.name("remove")).click();
        goTo("http://localhost:" + port + "/kayttajat/test1005");
        assertThat(pageSource()).doesNotContain("testiKuva");
        
        pictureRepository.delete(picture);
    }

    
    
    private void enterDetailsAndSubmit(String username, String password) {
        find(By.name("username")).fill().with(username);
        find(By.name("password")).fill().with(password);
        find(By.name("password")).submit();
    }
     
}
