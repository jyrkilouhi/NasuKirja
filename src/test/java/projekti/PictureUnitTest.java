package projekti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PictureUnitTest  {
      
    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(800, 805);
        createFriends(800, 801);
        createFriends(801, 802);
    }
    
    @After
    public void removeTestUsersAndFriends() {
        deleteFriends(800, 801);
        deleteFriends(801, 802);
        for(int id = 800; id <= 805; id++) {
            Account test = accountRepository.findByProfilename("test" + id );
            accountRepository.delete(test);
        }
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Picture Unit Testaaja (test" + id + ")");
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
    
        
    private void deleteFriends(int id1, int id2) {
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        Friend test = friendRepository.findByAskedbyAndAskedfrom(account1, account2);
        if(test != null) friendRepository.delete(test);
    }
    
    private Picture createPicture(String message, int ownerId) {
        Picture picture = new Picture();
        picture.setText(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        picture.setOwner(owner);
        return picture;
    }
    
    @Test
    public void canSavePicture() {
        int picturesBefore = pictureRepository.findAll().size();
        Picture picture = createPicture("Testi Tesi testi" , 800);
        pictureRepository.save(picture);
        assertTrue("Picture can be added", pictureRepository.findAll().size() == messagesBefore + 1); 
        pictureRepository.delete(picture);
    }

    @Test
    public void canFindSavedPicture() {
        Picture picture = createPicture("Testi Tesi testi" , 800);
        pictureRepository.save(picture);
        Account owner = accountRepository.findByProfilename("test" + 800);
        List <Picture> found = pictureRepository.findByOwner(owner);
        assertTrue("Can found  added picture", found.size() == 1); 
        pictureRepository.delete(picture);
    }
    
}
