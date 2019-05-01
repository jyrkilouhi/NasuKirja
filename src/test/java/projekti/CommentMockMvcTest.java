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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommentMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
        
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
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsers() {
        createTestUsers(2500, 2550);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Comment MockMvc Testaaja (test" + id + ")");
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
    @WithMockUser(username = "testi2500")
    public void canCommentOwnWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2500");
        int messagesBefore = wallRepository.findByOwner(account1).size();
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        
        mockMvc.perform(get("/kayttajat/test2500")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/test2500").param("newWallMessage", "testi viesti testi vain"))
              .andExpect(redirectedUrl("/kayttajat/test2500"));
        List <Wall> tests = wallRepository.findByOwner(account1);
        assertTrue("Wall message written to repository" , tests.size() == messagesBefore + 1);
        
        mockMvc.perform(get("/kayttajat/test2500")).andExpect(status().isOk());
        System.out.println("/kayttajat/wall/comment/" + tests.get(0).getId() + "/test2500");
        mockMvc.perform(post("/kayttajat/wall/comment/" + tests.get(0).getId() + "/test2500").param("newWallComment", "Comment2500"))
              .andExpect(redirectedUrl("/kayttajat/test2500"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Wall comment written to repository" , comments.size() == commentsBefore + 1);
        
        assertTrue("Comment visible at wall", mockMvc.perform(get("/kayttajat/test2500"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("Comment2500"));
    } 
    
    @Test
    @WithMockUser(username = "testi2501")
    public void canCommentFriendWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2501");
        Account account2 = accountRepository.findByProfilename("test2502");
        createFriends(2501, 2502);
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Wall test = createWall("Test tetsi", 2502, 2502);
        
        mockMvc.perform(get("/kayttajat/test2502")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/comment/" + test.getId() + "/test2502").param("newWallComment", "Comment2502"))
              .andExpect(redirectedUrl("/kayttajat/test2502"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Wall comment written to repository" , comments.size() == commentsBefore + 1);
        
        assertTrue("Comment visible at wall", mockMvc.perform(get("/kayttajat/test2502"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("Comment2502"));
    } 
    
    @Test
    @WithMockUser(username = "testi2503")
    public void cannotCommentNonFriendWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2503");
        Account account2 = accountRepository.findByProfilename("test2504");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Wall test = createWall("Test tetsi", 2504, 2504);
        
        mockMvc.perform(get("/kayttajat/test2504")).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/comment/" + test.getId() + "/test2504").param("newWallComment", "Comment2504"))
              .andExpect(redirectedUrl("/kayttajat/test2504"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Wall comment should NOT written to repository" , comments.size() == commentsBefore);
        
    } 
    
    
        @Test
    @WithMockUser(username = "testi2510")
    public void canCommentOwnPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2510");

        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Picture test = createPicture("Test tetsi", 2510);
        
        mockMvc.perform(get("/kayttajat/test2510")).andExpect(status().isOk());
        mockMvc.perform(post("/picture/comment/" + test.getId() + "/test2510").param("newPictureComment", "Comment2510"))
              .andExpect(redirectedUrl("/kayttajat/test2510"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Picture comment written to repository" , comments.size() == commentsBefore + 1);
        
        assertTrue("Comment visible at wall", mockMvc.perform(get("/kayttajat/test2510"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("Comment2510"));
    } 
    
    @Test
    @WithMockUser(username = "testi2511")
    public void canCommentFriendPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2511");
        Account account2 = accountRepository.findByProfilename("test2512");
        createFriends(2511, 2512);
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Picture test = createPicture("Test tetsi", 2512);
        
        mockMvc.perform(get("/kayttajat/test2512")).andExpect(status().isOk());
        mockMvc.perform(post("/picture/comment/" + test.getId() + "/test2512").param("newPictureComment", "Comment2512"))
              .andExpect(redirectedUrl("/kayttajat/test2512"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Picture comment written to repository" , comments.size() == commentsBefore + 1);
        
        assertTrue("Comment visible at wall", mockMvc.perform(get("/kayttajat/test2512"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("Comment2512"));
    } 
    
    @Test
    @WithMockUser(username = "testi2513")
    public void cannotCommentNonFriendPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2513");
        Account account2 = accountRepository.findByProfilename("test2514");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Picture test = createPicture("Test tetsi", 2514);
        
        mockMvc.perform(get("/kayttajat/test2514")).andExpect(status().isOk());
        mockMvc.perform(post("/picture/comment/" + test.getId() + "/test2514").param("newPictureComment", "Comment2514"))
              .andExpect(redirectedUrl("/kayttajat/test2514"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Picture comment should NOT be written to repository" , comments.size() == commentsBefore );
    } 

               
    @Test
    @WithMockUser(username = "testi2505")
    public void cannotCommentWrongWallMessage() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2505");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Wall test = createWall("Test tetsi", 2505, 2505);
        
        mockMvc.perform(post("/kayttajat/wall/comment/" + 1 + "/test2505").param("newWallComment", "Comment2505"))
              .andExpect(redirectedUrl("/kayttajat/test2505"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Wall comment should NOT written to repository" , comments.size() == commentsBefore);
        // Hyvä testi, koodista löytyi oikea virhe!!
    } 
    
    @Test
    @WithMockUser(username = "testi2515")
    public void cannotCommentWrongPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2515");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Picture test = createPicture("Test tetsi", 2515);
        
        mockMvc.perform(get("/kayttajat/test2515")).andExpect(status().isOk());
        mockMvc.perform(post("/picture/comment/" + 1 + "/test2515").param("newPictureComment", "Comment2515"))
              .andExpect(redirectedUrl("/kayttajat/test2515"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Picture comment should NOT be written to repository" , comments.size() == commentsBefore );
    } 

    @Test
    public void anonymousCannotCommentPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2519");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Picture test = createPicture("Test tetsi", 2519);
        
        mockMvc.perform(post("/picture/comment/" + test.getId() + "/test2519").param("newPictureComment", "Comment2519"))
              .andExpect(redirectedUrl("http://localhost/login"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Picture comment should NOT be written to repository" , comments.size() == commentsBefore );
    } 
    
    @Test
    public void anonymousCannotCommentWall() throws Exception {
        Account account1 = accountRepository.findByProfilename("test2506");
        int commentsBefore = commentRepository.findByCommenter(account1).size();
        Wall test = createWall("Test tetsi", 2506, 2506);
        
        mockMvc.perform(post("/kayttajat/wall/comment/" + test.getId() + "/test2506").param("newWallComment", "Comment2504"))
              .andExpect(redirectedUrl("http://localhost/login"));
        List <Comment> comments = commentRepository.findByCommenter(account1);
        assertTrue("Wall comment should NOT written to repository" , comments.size() == commentsBefore);
    }
    
    @Test
    public void anonymousCannotReadWallOrPicturesOrComments() throws Exception {
        mockMvc.perform(get("/kayttajat/wall/test2520"))
              .andExpect(redirectedUrl("http://localhost/login"));
    }
    
}
