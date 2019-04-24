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
    private AccountService accountService;

    @GetMapping("/")
    public String viewRootPage(Model model) {
        model = accountService.addAuthenticationName(model);      
        model.addAttribute("numberOfAccounts", accountService.numberOfAccounts());
        return "index";
    }
    
    @GetMapping("/help")
    public String viewHelpPage(Model model) {
        model = accountService.addAuthenticationName(model);      
        return "help";
    }
    
    @GetMapping("/omasivu")
    public String viewMyPage(Model model) {
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
}
