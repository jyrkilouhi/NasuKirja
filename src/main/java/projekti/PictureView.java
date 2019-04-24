package projekti;

public class PictureView {
    private long id;    
    private String text; 
    private Account owner;
    private long likes;
    private Boolean hasLiked;

    public PictureView(long id, String text, Account owner, long likes, Boolean hasLiked) {
        this.id = id;
        this.text = text;
        this.owner = owner;
        this.likes = likes;
        this.hasLiked = hasLiked;
    }
    
    public PictureView(Picture picture, long likes, Boolean hasLiked) {
        this.id = picture.getId();
        this.text = picture.getText();
        this.owner = picture.getOwner();
        this.likes = likes;
        this.hasLiked = hasLiked;
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
