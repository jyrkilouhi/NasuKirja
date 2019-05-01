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
public class CommentUnitTest  {
      
    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private FriendService friendService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(1400, 1420);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Coment Unit Testaaja (test" + id + ")");
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
    
    public Comment createWallComment(Wall wall, String commentText, Account account) {
            Comment newComment = new Comment();
            newComment.setCommenter(account);
            newComment.setContent(commentText);
            newComment.setWall(wall);
            newComment.setTime(LocalDateTime.now());
            commentRepository.save(newComment);     
            return newComment;
    }

    public Comment createPictureComment(Picture picture, String commentText, Account account) {
            Comment newComment = new Comment();
            newComment.setCommenter(account);
            newComment.setContent(commentText);
            newComment.setPicture(picture);
            newComment.setTime(LocalDateTime.now());
            commentRepository.save(newComment);     
            return newComment;
    }
    
    @Test
    public void canCommentWall() {
        int messagesBefore = wallRepository.findAll().size();
        Wall test = createWall("Testi Tesi testi" , 1400, 1400);
        assertTrue("Message to wall can be added", wallRepository.findAll().size() == messagesBefore + 1); 
        int commentsBefore = commentRepository.findAll().size();
        Comment comment = createWallComment(test, "Test comments" , accountRepository.findByProfilename("test" + 1400));
        assertTrue("Comment to wall can be added", commentRepository.findAll().size() == commentsBefore + 1);         
    }

    @Test
    public void canFindWallComment() {
        Wall test = createWall("Testi Tesi testi" , 1401, 1401);
        Comment comment = createWallComment(test, "Test comment 1234512345" , accountRepository.findByProfilename("test" + 1401));
        List <Comment> found = commentRepository.findByWall(test);
        assertTrue("Can found added wall Comment", found.size() == 1); 
    }
    
    @Test
    public void canCommentPicture() {
        int picturesBefore = pictureRepository.findAll().size();
        Picture test = createPicture("TestiKuva" , 1402);
        assertTrue("Picture can be added", pictureRepository.findAll().size() == picturesBefore + 1); 
        int commentsBefore = commentRepository.findAll().size();
        Comment comment = createPictureComment(test, "Test comments" , accountRepository.findByProfilename("test" + 1402));
        assertTrue("Comment to picture can be added", commentRepository.findAll().size() == commentsBefore + 1);         
    }
    
    @Test
    public void canFindPictureComment() {
        Picture test = createPicture("TesiKuva" , 1403);
        Comment comment = createPictureComment(test, "Test comment 1234512345" , accountRepository.findByProfilename("test" + 1403));
        List <Comment> found = commentRepository.findByPicture(test);
        assertTrue("Can found added wall Comment", found.size() == 1); 
    }
    
}
