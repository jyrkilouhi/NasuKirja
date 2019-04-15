package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserPageController {
       
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendService friendService;
    
    @GetMapping("/omasivu")
    public String viewOwnPage(Model model) {
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    @GetMapping("/kayttajat")
    public String viewAllUsers(Model model, String findname) {
        if(findname == null) findname="";
        model = accountService.findUsers(model, findname);
        return "listusers";
    }
       
    @GetMapping("/kayttajat/{profilename}")
    public String viewOneUser(Model model, @PathVariable String profilename) {
        model = accountService.findProfile(model, profilename);
        model = friendService.addFriendListAndStatus(model, profilename);
        return "userpage";
    }   
    
    @PostMapping("/kayttajat/{profilename}")
    public String askForFriend(Model model, @PathVariable String profilename) {
        friendService.askForFriend(profilename);
        return "redirect:/kayttajat/" + profilename;
    }

    @PostMapping("/kayttajat/approve/{profilename}")
    public String approveForFriend(Model model, @PathVariable String profilename, 
            @RequestParam(required=false, value="submit") String submit,
            @RequestParam(required=false, value="reject") String reject) {
        if(submit != null) friendService.makeFriend(profilename);
        if(reject != null) friendService.rejectFriend(profilename);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }

}
