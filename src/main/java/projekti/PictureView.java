package projekti;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureView {
    private long id;    
    private String text; 
    private Account owner;
    private long likes;
    private Boolean hasLiked;
    private List<Comment> comments;
   
    public PictureView(Picture picture, long likes, Boolean hasLiked, List<Comment> comments) {
        this.id = picture.getId();
        this.text = picture.getText();
        this.owner = picture.getOwner();
        this.likes = likes;
        this.hasLiked = hasLiked;
        this.comments = comments;
    }    
}
