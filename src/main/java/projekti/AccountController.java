
package projekti;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping("/accounts")
    public String view(@ModelAttribute Account account, Model model) {
        model.addAttribute("loggedUser", authenticationName());
        return "accountform";
    }

    @PostMapping("/accounts")
    public String register(@Valid @ModelAttribute Account account, BindingResult bindingResult, Model model) {
        
        if(bindingResult.hasErrors()) {
            return "accountform";
        }
        
        if(accountRepository.findByUsername(account.getUsername()) != null) {
            model.addAttribute("UserNameError", "Käyttäjänimi on jo varattu");
            return "accountform";           
        }
        if(accountRepository.findByProfilename(account.getProfilename()) != null) {
            model.addAttribute("ProfileNameError", "Antamasi profiili on jo käytössä");
            return "accountform";           
        }        
        
        String textPassword = account.getPassword();
        account.setPassword(passwordEncoder.encode(textPassword));
        accountRepository.save(account);
        return "redirect:/";
    }
    
    private String authenticationName() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         return auth.getName();
    }
    
}
