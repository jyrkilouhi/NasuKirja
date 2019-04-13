package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public String helloWorld(Model model) {
        model.addAttribute("userNumber", accountRepository.findAll().size());
        return "index";
    }
}
