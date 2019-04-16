package projekti;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class WallService {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private WallRepository wallRepository;
    
    public void newWallMessage(String profilename, String message) {
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null) return;
        if( friendService.areFriends(loggedAccount, profileAccount) || 
                loggedAccount.getProfilename().contentEquals(profileAccount.getProfilename())) {
        
            Wall newWallMessage = new Wall();
            newWallMessage.setMessage(message);
            newWallMessage.setMessager(loggedAccount);
            newWallMessage.setOwner(profileAccount);
            newWallMessage.setTime(LocalDateTime.now());
            wallRepository.save(newWallMessage);
        }
        
    }
    
    public Model addWallMessages(Model model, String profilename) {    
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null) return model;
        
        Pageable pageable = PageRequest.of(0, 25, Sort.by("time").descending());

        model.addAttribute("WallMessages", wallRepository.findByOwner(profileAccount , pageable));
        
        return model;
    }
    
}
