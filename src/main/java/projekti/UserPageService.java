package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class UserPageService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;

    public Model addAuthenticationName(Model model) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         model.addAttribute("loggedUser", auth.getName());
         return model;
    }    
    
    public Model findUsers(Model model, String findname) {
        model = addAuthenticationName(model);
        List <Account> foundUsers = accountRepository.findByRealnameContaining(findname);
        if(foundUsers.size() == 0) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " ei löydy yhtään käyttäjää");             
        }
        if(foundUsers.size() > 50) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " löytyi " + foundUsers.size() + " käyttäjää. Tarkenna hakua.");              
        } else {       
            model.addAttribute("userlist", foundUsers);
        }
        return model;
    }
    
    public Boolean isProfile(String profilename) {
        Account user = accountRepository.findByProfilename(profilename);
        return (user != null);         
    }
    
    public Model profileError(Model model, String profilename) {
        model = addAuthenticationName(model);
        model.addAttribute("FindUserError", "Antamaasi profiilia " + profilename + " ei löydy");               
        return model;
    } 
    
    public Model findProfile(Model model, String profilename) {
        model = addAuthenticationName(model);
        Account user = accountRepository.findByProfilename(profilename);
        if(user != null) {
            model.addAttribute("user", user);
        } else {
            model = profileError(model, profilename);         
        }        
        return model;
    }
    
}
