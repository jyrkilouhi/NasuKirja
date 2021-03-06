package projekti;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RootMockMvcTest  {
      
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void statusOkforRootPage() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }   
    
    @Test
    public void statusOkforHelpPage() throws Exception {
        mockMvc.perform(get("/help")).andExpect(status().isOk());
    } 
}
