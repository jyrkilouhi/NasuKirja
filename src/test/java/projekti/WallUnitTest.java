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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WallUnitTest  {
      
    @Autowired
    private WallRepository wallRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(400, 405);
        createFriends(400, 401);
        createFriends(401, 402);
    }

    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Wall Unit Testaaja (test" + id + ")");
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
    
    
    private Wall createWall(String message, int ownerId, int messagerId) {
        Wall wall = new Wall();
        wall.setTime(LocalDateTime.now());
        wall.setMessage(message);
        Account owner = accountRepository.findByProfilename("test" + ownerId);
        Account messager = accountRepository.findByProfilename("test" + messagerId);
        wall.setMessager(messager);
        wall.setOwner(owner);
        return wall;
    }
    
    @Test
    public void canWriteToWall() {
        int messagesBefore = wallRepository.findAll().size();
        Wall test = createWall("Testi Tesi testi" , 400, 400);
        wallRepository.save(test);
        assertTrue("Message to wall can be added", wallRepository.findAll().size() == messagesBefore + 1); 
        wallRepository.delete(test);
    }

    @Test
    public void canFindMessageFromOwnWall() {
        Wall test = createWall("Testi Tesi testi" , 400, 401);
        wallRepository.save(test);
        Account owner = accountRepository.findByProfilename("test" + 400);
        List <Wall> found = wallRepository.findByOwner(owner);
        assertTrue("Can found Message added to wall", found.size() == 1); 
        wallRepository.delete(test);
    }
    
}
