package projekti;

import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureService {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PictureRepository pictureRepository;

    
    public void newPicture(String message, MultipartFile file) throws IOException {
        Account loggedAccount = accountService.loggedInAccount();
        if(loggedAccount == null) return;
        if(pictureRepository.findByOwner(loggedAccount).size() >= 10) return;
        System.out.println("NEW PICTURE TYPE " + file.getContentType());
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
            // TODO: poista kommentit ja tykkäykset!!
            if(loggedAccount.getProfilePicture().getId() == id) {
                loggedAccount.setProfilePicture(null);
                accountRepository.save(loggedAccount);
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
        Account profileAccount = accountRepository.findByProfilename(profilename);
        if(profileAccount == null) return model;
        model.addAttribute("Pictures", pictureRepository.findByOwner(profileAccount));
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
    
}
