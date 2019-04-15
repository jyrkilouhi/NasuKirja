package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    public Model addAuthenticationName(Model model) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         model.addAttribute("loggedUser", auth.getName());
         return model;
    }    
    
    public Account loggedInAccount() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         Account account = accountRepository.findByUsername(auth.getName());
         return account;
    } 
    
    public Model findUsers(Model model, String findname) {
        model = addAuthenticationName(model);
        List <Account> foundUsers = accountRepository.findByRealnameContaining(findname);
        if(foundUsers.isEmpty()) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " ei löydy yhtään käyttäjää");             
        }
        if(foundUsers.size() > 50) {
            model.addAttribute("FindUserError", "Antamallasi hakuehdolla " + findname + " löytyi " + foundUsers.size() + " käyttäjää. Tarkenna hakua.");              
        } else {       
            model.addAttribute("userlist", foundUsers);
        }
        return model;
    }    
    
    public Model profileError(Model model, String profilename) {
        model = addAuthenticationName(model);
        model.addAttribute("FindUserError", "NasuKirjasta ei löydy profiilia " + profilename);  
        Account errorUser = new Account();
        errorUser.setRealname("User not found");
        model.addAttribute("user", errorUser);
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
    
    public Boolean accountIsOkToBeAdded(Account account) {
        if(accountRepository.findByUsername(account.getUsername()) != null) return false;
        if(accountRepository.findByProfilename(account.getProfilename()) != null) return false;      
        return true;
    }
    
    public Model accountError(Model model, Account account) {
        if(accountRepository.findByUsername(account.getUsername()) != null) {
            model.addAttribute("UserNameError", "Käyttäjänimi on jo varattu");      
        }
        if(accountRepository.findByProfilename(account.getProfilename()) != null) {
            model.addAttribute("ProfileNameError", "Antamasi profiili on jo käytössä");        
        }  
        return model;
    }
    
    public void saveNewAccount(Account account) {
        String textPassword = account.getPassword();
        account.setPassword(passwordEncoder.encode(textPassword));
        accountRepository.save(account);        
    }
}
