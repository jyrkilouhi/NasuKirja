package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public String helloWorld(Model model) {
        model.addAttribute("loggedUser", authenticationName());        
        model.addAttribute("userNumber", accountRepository.findAll().size());
        return "index";
    }
    
    private String authenticationName() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         return auth.getName();
    }
}
