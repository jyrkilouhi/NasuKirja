package projekti;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PictureMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    private WallRepository wallRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Before
    public void initTestUsersAndFriends() {
        createTestUsers(900, 915);

    }
    
    private void createTestUsers(int alku, int loppu) {
        for(int id = alku; id <= loppu; id++) {
            if(accountRepository.findByProfilename("test" + id) == null) {
                Account test = new Account();
                test.setRealname("Picture MockMvc Testaaja (test" + id + ")");
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
    @WithMockUser(username = "testi901")
    public void canSaveGiPictureToOwnAlbum() throws Exception {
        Account account1 = accountRepository.findByProfilename("test901");
        int picturesBefore = pictureRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test901")).andExpect(status().isOk());
       
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testest.gif", "image/gif", "TestPicture".getBytes());
        mockMvc.perform(multipart("/picture/sendpicture").file(multipartFile).param("newPictureMessage","TestPictureMessage"))
                .andExpect(redirectedUrl("/kayttajat/test901"));
        
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture saved to repository" , tests.size() == picturesBefore + 1);
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi902")
    public void canSaveJpegPictureToOwnAlbum() throws Exception {
        Account account1 = accountRepository.findByProfilename("test902");
        int picturesBefore = pictureRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test902")).andExpect(status().isOk());
       
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testest.jpeg", "image/jpeg", "TestiKuva".getBytes());
        mockMvc.perform(multipart("/picture/sendpicture").file(multipartFile).param("newPictureMessage","TestPictureMessage"))
                .andExpect(redirectedUrl("/kayttajat/test902"));
        
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture saved to repository" , tests.size() == picturesBefore + 1);
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi903")
    public void cannotSaveEmptyPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test903");
        int picturesBefore = pictureRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test903")).andExpect(status().isOk());
       
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testest.gif", "image/gif", "".getBytes());
        mockMvc.perform(multipart("/picture/sendpicture").file(multipartFile).param("newPictureMessage","TestPictureMessage"))
                .andExpect(redirectedUrl("/kayttajat/test903"));
        
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture not saved to repository" , tests.size() == picturesBefore );
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 

    
    @Test
    @WithMockUser(username = "testi904")
    public void canSeeOwnPictures() throws Exception {
        Account account1 = accountRepository.findByProfilename("test904");
        int picturesBefore = pictureRepository.findByOwner(account1).size();
        mockMvc.perform(get("/kayttajat/test904")).andExpect(status().isOk());
       
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testest.gif", "image/gif", "TestiKuva".getBytes());
        mockMvc.perform(multipart("/picture/sendpicture").file(multipartFile).param("newPictureMessage","TestPictureMessage"))
                .andExpect(redirectedUrl("/kayttajat/test904"));
        
        MvcResult result = mockMvc.perform(get("/kayttajat/test904")).andReturn();
        List<Picture> pictures = (List)result.getModelAndView().getModel().get("Pictures");
        assertTrue("Picture visible at page", pictures.size() == 1); 
        
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture saved to repository" , tests.size() == picturesBefore + 1);
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi905")
    public void canDeleteOwnPicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test905");
        int picturesBefore = pictureRepository.findByOwner(account1).size();

        Picture picture = createPicture("testiKuva", 905);
        pictureRepository.save(picture);
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture added to repository" , tests.size() == picturesBefore + 1);        

        String postUrl = "/picture/remove/" + picture.getId().toString();
        mockMvc.perform(post(postUrl)).andExpect(redirectedUrl("/kayttajat/test905"));
        
        tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture removed from repository" , tests.size() == picturesBefore );
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 
    
    @Test
    @WithMockUser(username = "testi906")
    public void canSetProfilePicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test906");
        int picturesBefore = pictureRepository.findByOwner(account1).size();

        Picture picture = createPicture("testiKuva", 906);
        pictureRepository.save(picture);
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture added to repository" , tests.size() == picturesBefore + 1);        

        String postUrl = "/picture/setprofile/" + picture.getId();
        mockMvc.perform(post(postUrl)).andExpect(redirectedUrl("/kayttajat/test906"));

        account1 = accountRepository.findByProfilename("test906");        
        assertTrue("Picture set as profile picture" , (long)picture.getId() == account1.getProfilePicture().getId());
        account1.setProfilePicture(null);
        accountRepository.save(account1);

        account1 = accountRepository.findByProfilename("test906");         
        tests = pictureRepository.findByOwner(account1);
        for(Picture test : tests) {
            pictureRepository.delete(test);  
        }
    }
    
    @Test
    @WithMockUser(username = "testi907")
    public void cannotSetOtherUsersProfilePicture() throws Exception {
        Account account1 = accountRepository.findByProfilename("test908");
        int picturesBefore = pictureRepository.findByOwner(account1).size();

        Picture picture = createPicture("testiKuva", 908);
        pictureRepository.save(picture);
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture added to repository" , tests.size() == picturesBefore + 1);        

        String postUrl = "/picture/setprofile/" + picture.getId().toString();
        mockMvc.perform(post(postUrl)).andExpect(redirectedUrl("/kayttajat/test907"));
        
        account1 = accountRepository.findByProfilename("test907");        
        assertTrue("Picture not set as profile picture" , account1.getProfilePicture() == null);

        account1 = accountRepository.findByProfilename("test908");        
        assertTrue("Picture not set as profile picture" , account1.getProfilePicture() == null);
        
        tests = pictureRepository.findByOwner(account1);
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 
    
    
    @Test
    @WithMockUser(username = "testi909")
    public void cannotDeleteOtherUsersPictures() throws Exception {
        Account account1 = accountRepository.findByProfilename("test910");
        int picturesBefore = pictureRepository.findByOwner(account1).size();

        Picture picture = createPicture("testiKuva", 910);
        pictureRepository.save(picture);
        List <Picture> tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture added to repository" , tests.size() == picturesBefore + 1);        

        String postUrl = "/picture/remove/" + picture.getId().toString();
        mockMvc.perform(post(postUrl)).andExpect(redirectedUrl("/kayttajat/test909"));
        
        tests = pictureRepository.findByOwner(account1);
        assertTrue("Picture not removed from repository" , tests.size() == picturesBefore + 1 );
        for(Picture test : tests) {
            pictureRepository.delete(test);
        }
    } 

    
    @Test
    @WithMockUser(username = "testi911")
    public void canSeeOtherUsersPictures() throws Exception {
        Picture picture = createPicture("testiKuva", 912);
        pictureRepository.save(picture);
        
        MvcResult result = mockMvc.perform(get("/kayttajat/test912")).andReturn();
        List<Picture> messages = (List)result.getModelAndView().getModel().get("Pictures");
        assertTrue("Picture visible  ", messages.size() == 1); 

        pictureRepository.delete(picture);
    } 
    
    
    @Test
    public void anonymousCannotSavePicture() throws Exception {
        Long picturesBefore = pictureRepository.count();
       
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testest.gif", "image/gif", "".getBytes());
        mockMvc.perform(multipart("/picture/sendpicture").file(multipartFile).param("newPictureMessage","TestPictureMessage"))
                .andExpect(redirectedUrl("http://localhost/login"));
        
        assertTrue("Picture not saved to repository" , pictureRepository.count() == picturesBefore );

    } 
    
    @Test
    public void anonymousCannotSeePictures() throws Exception {
        mockMvc.perform(get("/kayttajat/wall/test915"))
              .andExpect(redirectedUrl("http://localhost/login"));
    }
    
}
