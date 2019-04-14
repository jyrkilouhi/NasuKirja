package projekti;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserPageController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    private UserPageService userPageService;
    
    @GetMapping("/kayttajat")
    public String view(Model model) {
        model = userPageService.addAuthenticationName(model);
        return "listusers";
    }
    
    @PostMapping("/kayttajat")
    public String find(Model model, String findname) {
        model = userPageService.findUsers(model, findname);
        return "listusers";
    }
    
    @GetMapping("/kayttajat/{profilename}")
    public String view(Model model, @PathVariable String profilename) {
        model = userPageService.findProfile(model, profilename);
        return "userpage";
    }      
}
