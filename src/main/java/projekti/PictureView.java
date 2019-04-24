package projekti;

import java.util.List;

public class PictureView {
    private long id;    
    private String text; 
    private Account owner;
    private long likes;
    private Boolean hasLiked;
    private List<Comment> comments;

    public PictureView(long id, String text, Account owner, long likes, Boolean hasLiked, List<Comment> comments) {
        this.id = id;
        this.text = text;
        this.owner = owner;
        this.likes = likes;
        this.hasLiked = hasLiked;
        this.comments = comments;
    }
    
    public PictureView(Picture picture, long likes, Boolean hasLiked, List<Comment> comments) {
        this.id = picture.getId();
        this.text = picture.getText();
        this.owner = picture.getOwner();
        this.likes = likes;
        this.hasLiked = hasLiked;
        this.comments = comments;
    }   

    public List<Comment> getComments() {
        return comments;
    }
    
    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Account getOwner() {
        return owner;
    }

    public long getLikes() {
        return likes;
    }

    public Boolean getHasLiked() {
        return hasLiked;
    }   
    
}
