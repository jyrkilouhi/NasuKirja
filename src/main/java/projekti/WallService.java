package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private LoveRepository likeRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    
    public void newWallMessage(String profilename, String message) {
        if(message.trim().isEmpty()) return;
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null || loggedAccount == null) return;
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
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null) return model;
        
        Pageable pageable = PageRequest.of(0, 25, Sort.by("time").descending());

        List<WallView> toWall = new ArrayList<>();
        List<Wall> wallMessages = wallRepository.findByOwner(profileAccount , pageable);
        for(Wall wallMesssage : wallMessages) {
            long likes = likeRepository.countByWall(wallMesssage);
            Boolean loggedAccountHasLiked = null;
            if(likeRepository.findByLoverAndWall(loggedAccount, wallMesssage).size() > 0 ) loggedAccountHasLiked = true;
            toWall.add(new WallView(wallMesssage, likes, loggedAccountHasLiked));
        }
        
        model.addAttribute("WallMessages", toWall);
        
        return model;
    }
    
    public void likeWallMessage(long id) {
        Wall message = wallRepository.getOne(id);
        if(message == null) return;
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = message.getOwner();
        if(profileAccount == null || loggedAccount == null) return;
        if( friendService.areFriends(loggedAccount, profileAccount) || 
                loggedAccount.getProfilename().contentEquals(profileAccount.getProfilename())) {  
            if(likeRepository.findByLoverAndWall(loggedAccount, message).size() == 0) {
                Love newLike = new Love();
                newLike.setWall(message);
                newLike.setLover(loggedAccount);
                likeRepository.save(newLike);
            }
        }
    }
    
    public void commentWallMessage(long id, String commentText) {
        Wall message = wallRepository.getOne(id);
        if(message == null) return;
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = message.getOwner();
        if(profileAccount == null || loggedAccount == null) return;
        if( friendService.areFriends(loggedAccount, profileAccount) || 
                loggedAccount.getProfilename().contentEquals(profileAccount.getProfilename())) {  
            Comment newComment = new Comment();
            newComment.setCommenter(loggedAccount);
            newComment.setContent(commentText);
            newComment.setWall(message);
            commentRepository.save(newComment);
        }         
    }
    
}
