package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class FriendService {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private FriendsRepository friendRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    public Boolean areFriends(Account account1, Account account2) {
        if((account1 == null) || (account2 == null)) return false;
        Friend friend1 = friendRepository.findByAskedbyAndAskedfrom(account1, account2);
        if(friend1 != null) return (friend1.isStatus() == true);
        Friend friend2 = friendRepository.findByAskedbyAndAskedfrom(account2, account1);
        if(friend2 != null) return (friend2.isStatus() == true);
        return false;
    } 
    
    public Boolean isAskedToBeFriend(Account account1, Account account2) {
        if((account1 == null) || (account2 == null)) return false;
        Friend friend = friendRepository.findByAskedbyAndAskedfrom(account1, account2);
        if(friend != null) return (friend.isStatus() == false);
        return false;
    } 
    
    public void askForFriend(String profilename) {
        Account loggedAccount = accountService.loggedInAccount();
        Account askedAccount = accountRepository.findByProfilename(profilename);
        if((askedAccount == null) || (loggedAccount == null)) return;
        if(areFriends(loggedAccount, askedAccount)) return;
        if(isAskedToBeFriend(loggedAccount, askedAccount)) return;
        if(loggedAccount.getProfilename().contentEquals(profilename)) return;
        
        Friend friendRequest = new Friend();
        friendRequest.setAskedby(loggedAccount);
        friendRequest.setAskedfrom(askedAccount);
        friendRequest.setStatus(false);
        friendRequest.setAsktime(LocalTime.now());
        friendRequest.setAskdate(LocalDate.now());
        friendRepository.save(friendRequest);
    }
    
    public Model addFriendListAndStatus(Model model, String profilename) {
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null || loggedAccount == null) return model;
        if(areFriends(loggedAccount, profileAccount)) {
            model.addAttribute("IsFriend", "True");              
        }
        if(isAskedToBeFriend(loggedAccount, profileAccount)) {
            model.addAttribute("IsAskedForFriend", "True");              
        }
        if(loggedAccount.getProfilename().contentEquals(profilename)) {
            model.addAttribute("IsMyPage", "True");              
        }
        
        model.addAttribute("FriendRequests", friendRepository.findByAskedfromAndStatus(profileAccount, false));
        List<Friend> friends1 = friendRepository.findByAskedfromAndStatus(profileAccount, true);
        List<Friend> friends2 = friendRepository.findByAskedbyAndStatus(profileAccount, true);
        for(Friend friend : friends2) {
            friend.setAskedby(friend.getAskedfrom());
        }
        friends1.addAll(friends2);
        model.addAttribute("Friends", friends1);
        
        return model;
    }
    
    public void makeFriend(String profilename) {
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null || loggedAccount == null) return;        
        if(areFriends(loggedAccount, profileAccount)) return;
        if(loggedAccount.getProfilename().contentEquals(profilename)) return;
        if(!isAskedToBeFriend(profileAccount, loggedAccount)) return;
        
        Friend friendRequest = friendRepository.findByAskedbyAndAskedfrom(profileAccount, loggedAccount);
        friendRequest.setStatus(true);
        friendRepository.save(friendRequest);
        
        friendRequest = friendRepository.findByAskedbyAndAskedfrom(loggedAccount, profileAccount);
        if(friendRequest != null) friendRepository.delete(friendRequest);
    }
    
    public void rejectFriend(String profilename) {
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null || loggedAccount == null) return;   
        if(areFriends(loggedAccount, profileAccount)) return;
        if(loggedAccount.getProfilename().contentEquals(profilename)) return;
        if(!isAskedToBeFriend(profileAccount, loggedAccount)) return;
        
        Friend friendRequest = friendRepository.findByAskedbyAndAskedfrom(profileAccount, loggedAccount);
        friendRepository.delete(friendRequest);
    }
    
}
