package projekti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LoveUnitTest  {
      
    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoveRepository loveRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(1500, 1520);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Love Unit Testaaja (test" + id + ")");
                test.setUsername("testi" + id);
                test.setProfilename("test" + id);
                test.setPassword(passwordEncoder.encode("test12345")); 
                accountRepository.save(test);
            }
        }
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
    
    private Picture createPicture(String message, int ownerId) {
        Picture picture = new Picture();
        picture.setText(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        picture.setOwner(owner);
        pictureRepository.save(picture);
        return picture;
    }
    
    public Love createWallLike(Wall wall, Account account) {
            Love love = new Love();    
            love.setLover(account);
            love.setWall(wall);
            loveRepository.save(love);
            return love;
    }

    public Love createPictureLike(Picture picture, Account account) {
            Love love = new Love();    
            love.setLover(account);
            love.setPicture(picture);
            loveRepository.save(love);
            return love;
    }
    
    @Test
    public void canLikeWall() {
        int messagesBefore = wallRepository.findAll().size();
        Wall test = createWall("Testi Tesi testi" , 1500, 1500);
        assertTrue("Message to wall can be added", wallRepository.findAll().size() == messagesBefore + 1); 
        int likesBefore = loveRepository.findAll().size();
        Love like = createWallLike(test,  accountRepository.findByProfilename("test" + 1500));
        assertTrue("Wall can be liked", loveRepository.findAll().size() == likesBefore + 1);         
    }

    @Test
    public void canFindWallLike() {
        Wall test = createWall("Testi Tesi testi" , 1501, 1501);
        Love like = createWallLike(test, accountRepository.findByProfilename("test" + 1401));
        List <Love> found = loveRepository.findByWall(test);
        assertTrue("Can found added wall Comment", found.size() == 1); 
    }
    
    @Test
    public void canLikePicture() {
        int picturesBefore = pictureRepository.findAll().size();
        Picture test = createPicture("TestiKuva" , 1502);
        assertTrue("Picture can be added", pictureRepository.findAll().size() == picturesBefore + 1); 
        int likesBefore = loveRepository.findAll().size();
        Love like = createPictureLike(test, accountRepository.findByProfilename("test" + 1502));
        assertTrue("Comment to picture can be added", loveRepository.findAll().size() == likesBefore + 1);         
    }
    
    @Test
    public void canFindPictureLike() {
        Picture test = createPicture("TesiKuva" , 1503);
        Love like = createPictureLike(test,  accountRepository.findByProfilename("test" + 1503));
        List <Love> found = loveRepository.findByPicture(test);
        assertTrue("Can found added wall Comment", found.size() == 1); 
    }
    
}
