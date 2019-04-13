package projekti;

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
public class UsersController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @GetMapping("/kayttajat")
    public String view() {
        return "userform";
    }
    
    @PostMapping("/kayttajat")
    public String find(Model model, String findname) {
        model.addAttribute("loggedUser", authenticationName());
        model.addAttribute("userlist", accountRepository.findByRealnameContaining(findname));
        return "userform";
    }
    
    @GetMapping("/kayttajat/{profilename}")
    public String view(Model model, @PathVariable String profilename) {
        model.addAttribute("loggedUser", authenticationName());
        model.addAttribute("user", accountRepository.findByProfilename(profilename));
        return "userpage";
    }
    
    private String authenticationName() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         return auth.getName();
    }
    
}
