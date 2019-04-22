package projekti;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserPageController {
       
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    private WallService wallService;
    
    @Autowired
    private PictureService pictureService;
    
    @GetMapping("/kayttajat/{profilename}")
    public String viewUserPage(Model model, @PathVariable String profilename) {
        model = accountService.findProfile(model, profilename);
        model = friendService.addFriendListAndStatus(model, profilename);
        model = wallService.addWallMessages(model, profilename);
        model = pictureService.addPictures(model, profilename);
        return "userpage";
    }   
    
    @PostMapping("/kayttajat/request/{profilename}")
    public String askForFriend(Model model, @PathVariable String profilename) {
        friendService.askForFriend(profilename);
        return "redirect:/kayttajat/" + profilename;
    }

    @PostMapping("/kayttajat/approve/{profilename}")
    public String approveForFriend(Model model, @PathVariable String profilename) {
        friendService.makeFriend(profilename);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    @PostMapping("/kayttajat/reject/{profilename}")
    public String rejectFriendRequest(Model model, @PathVariable String profilename) {
        friendService.rejectFriend(profilename);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    @PostMapping("/kayttajat/wall/{profilename}")
    public String writeToWall(Model model, @PathVariable String profilename, @RequestParam String newWallMessage) {
        wallService.newWallMessage(profilename, newWallMessage);
        return "redirect:/kayttajat/" + profilename;
    }
    
    @PostMapping("/kayttajat/sendpicture")
    public String sendNewPicture(Model model,  @RequestParam String newPictureMessage, @RequestParam("file") MultipartFile newPictureFile) throws IOException {
        pictureService.newPicture(newPictureMessage, newPictureFile);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    @GetMapping(path = "/picture/{id}", produces = "image/gif")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return pictureService.getOnePicture(id);
    } 
    
    @PostMapping("/picture/remove/{id}")
    public String removePicture(Model model, @PathVariable Long id) {
        pictureService.deletePicture(id);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    @PostMapping("/picture/setprofile/{id}")
    public String setProfilePicture(Model model, @PathVariable Long id) {
        pictureService.setProfilePicture(id);
        Account loggedAccount = accountService.loggedInAccount();
        return "redirect:/kayttajat/" + loggedAccount.getProfilename();
    }
    
    
    

}
