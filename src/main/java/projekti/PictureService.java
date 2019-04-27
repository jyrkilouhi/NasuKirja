package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureService {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendService friendService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LoveRepository likeRepository;
    
    
    public void newPicture(String message, MultipartFile file) throws IOException {
        if (file.getSize() == 0) return;
        if (file.getSize() > 1000000) return;
        Account loggedAccount = accountService.loggedInAccount();
        if(loggedAccount == null) return;
        if(pictureRepository.findByOwner(loggedAccount).size() >= 10) return;
        if( !file.getContentType().contains("gif") && !file.getContentType().contains("jpeg") && !file.getContentType().contains("png")) return;
     
        Picture newPicture = new Picture();
        newPicture.setText(message);
        newPicture.setOwner(loggedAccount);
        newPicture.setContent(file.getBytes());
        pictureRepository.save(newPicture);      
    }
    
    public void deletePicture(Long id) {
        Optional<Picture> picture = pictureRepository.findById(id);
        if( ! picture.isPresent() ) return;
        Account loggedAccount = accountService.loggedInAccount();
        if(loggedAccount == null) return;
        if(loggedAccount == picture.get().getOwner()) { 
            List<Love> loves = likeRepository.findByPicture(picture.get());
            for(Love love : loves) {
                likeRepository.delete(love);
            }
            List<Comment> comments = commentRepository.findByPicture(picture.get());
            for(Comment comment : comments) {
                commentRepository.delete(comment);
            }
            if(loggedAccount.getProfilePicture() != null) {
                if(loggedAccount.getProfilePicture().getId() == id) {
                    loggedAccount.setProfilePicture(null);
                    accountRepository.save(loggedAccount);
                }
            }
            pictureRepository.deleteById(id);
        }
    }
        
    public void setProfilePicture(Long id) {
        Optional<Picture> picture = pictureRepository.findById(id);
        if( ! picture.isPresent() ) return;
        Account loggedAccount = accountService.loggedInAccount();
        if(loggedAccount == null) return;
        if(loggedAccount == picture.get().getOwner()) { 
            loggedAccount.setProfilePicture(picture.get());
            accountRepository.save(loggedAccount);
        }
    }

    public Model addPictures(Model model, String profilename) {   
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null) return model;
        
        List<PictureView> wallPictures = new ArrayList<>();
        List<Picture> pictures = pictureRepository.findByOwner(profileAccount);
        for(Picture picture : pictures) {
            long likes = likeRepository.countByPicture(picture);
            Boolean loggedAccountHasLiked = null;
            if(likeRepository.findByLoverAndPicture(loggedAccount, picture).size() > 0 ) loggedAccountHasLiked = true;
            Pageable pageableComments = PageRequest.of(0, 10, Sort.by("time").descending());
            List<Comment> comments = commentRepository.findByPicture(picture, pageableComments);
            wallPictures.add(new PictureView(picture, likes, loggedAccountHasLiked, comments));
        }
        
        model.addAttribute("Pictures", wallPictures);        
        if(pictureRepository.findByOwner(profileAccount).size() < 10) {
            model.addAttribute("canAddPicture", true);
        }
        
        return model;
    }
    
    public byte[] getOnePicture(Long id) {
        if(pictureRepository.existsById(id)) {
            return pictureRepository.findById(id).get().getContent();
        }
        return "Error".getBytes();
    }
    
    public void likePicture(Long id) {
        Picture picture = pictureRepository.getOne(id);
        if(picture == null) return;
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = picture.getOwner();
        if(profileAccount == null || loggedAccount == null) return;
        if( friendService.areFriends(loggedAccount, profileAccount) || 
                loggedAccount.getProfilename().contentEquals(profileAccount.getProfilename())) {  
            if(likeRepository.findByLoverAndPicture(loggedAccount, picture).isEmpty()) {
                Love newLike = new Love();
                newLike.setPicture(picture);
                newLike.setLover(loggedAccount);
                likeRepository.save(newLike);
            }
        }        
    }
    
    public void commentPicture(Long id, String commentText) {
        if(commentText.trim().isEmpty()) return;
        if(commentText.length() > 250) commentText = commentText.substring(0, 250) + "...";
        Picture picture = pictureRepository.getOne(id);
        if(picture == null) return;
        Account loggedAccount = accountService.loggedInAccount();
        Account profileAccount = picture.getOwner();
        if(profileAccount == null || loggedAccount == null) return;
        if( friendService.areFriends(loggedAccount, profileAccount) || 
                loggedAccount.getProfilename().contentEquals(profileAccount.getProfilename())) {  
            Comment newComment = new Comment();
            newComment.setCommenter(loggedAccount);
            newComment.setContent(commentText);
            newComment.setPicture(picture);
            newComment.setTime(LocalDateTime.now());
            commentRepository.save(newComment);
        }         
    }
    
}
