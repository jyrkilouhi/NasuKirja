package projekti;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public String view(@ModelAttribute Account account, Model model) {
        model = accountService.addAuthenticationName(model);
        return "accountform";
    }

    @PostMapping("/accounts")
    public String register(@Valid @ModelAttribute Account account, BindingResult bindingResult, Model model) {        
        if(bindingResult.hasErrors()) {
            return "accountform";
        }
        account.setProfilename(account.getProfilename().toLowerCase());
        if(!accountService.accountIsOkToBeAdded(account)) {
            model = accountService.accountError(model, account);
            return "accountform";               
        }     
        accountService.saveNewAccount(account);
        return "redirect:/";
    }   
}
