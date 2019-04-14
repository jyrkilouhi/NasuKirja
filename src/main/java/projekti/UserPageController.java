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
    
    @GetMapping("/kayttajat")
    public String view(Model model) {
        model.addAttribute("loggedUser", authenticationName());
        return "userform";
    }
    
    @PostMapping("/kayttajat")
    public String find(Model model, String findname) {
        model.addAttribute("loggedUser", authenticationName());
        List <Account> foundUsers = accountRepository.findByRealnameContaining(findname);
        if(foundUsers.size() == 0) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " ei löydy yhtään käyttäjää");
            return "userform";                
        }
        if(foundUsers.size() > 50) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " löytyi " + foundUsers.size() + " käyttäjää. Tarkenna hakua.");
            return "userform";                
        }        
        model.addAttribute("userlist", foundUsers);
        return "userform";
    }
    
    @GetMapping("/kayttajat/{profilename}")
    public String view(Model model, @PathVariable String profilename) {
        model.addAttribute("loggedUser", authenticationName());
        Account user = accountRepository.findByProfilename(profilename);
        if(user != null) {
            model.addAttribute("user", user);
            return "userpage";
        } else {
            model.addAttribute("FindUserError", "Antamaasi profiilia " + profilename + " ei löydy");
            return "userform";            
        }
    }
    
    private String authenticationName() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         return auth.getName();
    }
    
}
