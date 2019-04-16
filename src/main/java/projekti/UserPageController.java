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
    
    @Autowired
    private WallService wallService;
           
           
    @GetMapping("/kayttajat/{profilename}")
    public String viewOneUser(Model model, @PathVariable String profilename) {
        model = accountService.findProfile(model, profilename);
        model = friendService.addFriendListAndStatus(model, profilename);
        model = wallService.addWallMessages(model, profilename);
        return "userpage";
    }   
    
    @PostMapping("/kayttajat/request/{profilename}")
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
    
    @PostMapping("/kayttajat/wall/{profilename}")
    public String writeToWall(Model model, @PathVariable String profilename, @RequestParam String newWallMessage) {
        wallService.newWallMessage(profilename, newWallMessage);
        return "redirect:/kayttajat/" + profilename;
    }

}
