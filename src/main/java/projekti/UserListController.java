package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserListController {
       
    @Autowired
    private AccountService accountService;
       
    @GetMapping("/kayttajat")
    public String viewAllUsers(Model model, String findname) {
        if(findname == null) findname="";
        model = accountService.findUsers(model, findname);
        return "userlist";
    }
}
