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
public class LoveMockMvcTest  {
      
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
    private LoveRepository loveRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsers() {
        createTestUsers(2600, 2650);
    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Love MockMvc Testaaja (test" + id + ")");
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
    @WithMockUser(username = "testi2600")
    public void canLikeOwnWall() throws Exception {
        int id1 = 2600;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Wall test = createWall("TestWall", id1, id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Wall like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));
    } 
    
    @Test
    @WithMockUser(username = "testi2602")
    public void canLikeFriendWall() throws Exception {
        int id1 = 2601;
        int id2 = 2602;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        createFriends(id1, id2);
        int likesBefore = loveRepository.findByLover(account2).size();
        Wall test = createWall("TestWall", id1, id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account2);
        assertTrue("Wall like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));
    } 
    
    @Test
    @WithMockUser(username = "testi2604")
    public void cannotLikeNonFriendWall() throws Exception {
        int id1 = 2603;
        int id2 = 2604;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        //createFriends(id1, id2);
        int likesBefore = loveRepository.findByLover(account2).size();
        Wall test = createWall("TestWall", id1, id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account2);
        assertTrue("Wall like should NOT be written to repository" , likes.size() == likesBefore );        
        assertTrue("like should not be visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("0 tykkää"));        
    } 

    @Test
    @WithMockUser(username = "testi2606")
    public void cannotLikeWallTwoTimes() throws Exception {
        int id1 = 2606;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Wall test = createWall("TestWall", id1, id1);        
        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Wall like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));        
        
        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        likes = loveRepository.findByLover(account1);
        assertTrue("Wall like written to only once repository" , likes.size() == likesBefore + 1);        
        assertTrue("ONE like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää")); 
    }  
    
    @Test
    @WithMockUser(username = "testi2608")
    public void cannotLikeWrongWall() throws Exception {
        int id1 = 2608;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Wall test = createWall("TestWall", id1, id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/kayttajat/wall/like/" + 1 + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Wall like should NOT be written to repository" , likes.size() == likesBefore );        
        assertTrue("like should not be visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("0 tykkää"));        
    } 
    
    @Test
    public void anonymousCannotLikeWall() throws Exception {
        int id1 = 2609;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Wall test = createWall("TestWall", id1, id1);        

        mockMvc.perform(post("/kayttajat/wall/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("http://localhost/login"));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Wall like should NOT be written to repository" , likes.size() == likesBefore );        

    }
    
    @Test
    @WithMockUser(username = "testi2610")
    public void canLikeOwnPicture() throws Exception {
        int id1 = 2610;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Picture test = createPicture("Test tetsi", id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Picture like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("Picture like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));
    } 
    
    @Test
    @WithMockUser(username = "testi2612")
    public void canLikeFriendPicture() throws Exception {
        int id1 = 2611;
        int id2 = 2612;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        createFriends(id2, id1);
        int likesBefore = loveRepository.findByLover(account2).size();
        Picture test = createPicture("Test tetsi", id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account2);
        assertTrue("Picture like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("Picture like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));
    } 
    
    @Test
    @WithMockUser(username = "testi2514")
    public void cannotLikeNonFriendPicture() throws Exception {
        int id1 = 2613;
        int id2 = 2614;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        Account account2 = accountRepository.findByProfilename("test" + id2);
        int likesBefore = loveRepository.findByLover(account2).size();
        Picture test = createPicture("Test tetsi", id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account2);
        assertTrue("Picture like not written to repository" , likes.size() == likesBefore );        
        assertTrue("Picture like not visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("0 tykkää"));
    } 
    
    @Test
    @WithMockUser(username = "testi2616")
    public void cannotLikePictureTwoTimes() throws Exception {
        int id1 = 2616;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Picture test = createPicture("Test tetsi", id1);        
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Picture like written to repository" , likes.size() == likesBefore + 1);        
        assertTrue("Picture like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));        
        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        likes = loveRepository.findByLover(account1);
        assertTrue("Picture like written once to repository" , likes.size() == likesBefore + 1);        
        assertTrue("One Picture like visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("1 tykkää"));  
    }              

    
    @Test
    @WithMockUser(username = "testi2518")
    public void cannotLikeWrongPicture() throws Exception {
        int id1 = 2618;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Picture test = createPicture("Test tetsi", id1);  
        mockMvc.perform(get("/kayttajat/test" + id1)).andExpect(status().isOk());
        mockMvc.perform(post("/picture/like/" + 1 + "/test" + id1))
              .andExpect(redirectedUrl("/kayttajat/test" + id1));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Picture like not written to repository" , likes.size() == likesBefore);        
        assertTrue("Picture like not visible at wall", mockMvc.perform(get("/kayttajat/test" + id1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString().contains("0 tykkää"));
    } 

    @Test
    public void anonymousCannotlikePicture() throws Exception {
        int id1 = 2620;
        Account account1 = accountRepository.findByProfilename("test" + id1);
        int likesBefore = loveRepository.findByLover(account1).size();
        Picture test = createPicture("Test tetsi", id1);  

        mockMvc.perform(post("/picture/like/" + test.getId() + "/test" + id1))
              .andExpect(redirectedUrl("http://localhost/login"));
        List <Love> likes = loveRepository.findByLover(account1);
        assertTrue("Picture like not written to repository" , likes.size() == likesBefore);        
    } 
    
    
    @Test
    public void anonymousCannotReadWallOrPicturesOrComments() throws Exception {
        mockMvc.perform(get("/kayttajat/wall/test2520"))
              .andExpect(redirectedUrl("http://localhost/login"));
    }
    
}
