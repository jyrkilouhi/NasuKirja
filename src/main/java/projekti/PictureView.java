package projekti;

public class PictureView {
    private long id;    
    private String text; 
    private byte[] content;
    private Account owner;
    private long likes;
    private Boolean hasLiked;

    public PictureView(long id, String text, byte[] content, Account owner, long likes, Boolean hasLiked) {
        this.id = id;
        this.text = text;
        this.content = content;
        this.owner = owner;
        this.likes = likes;
        this.hasLiked = hasLiked;
    }
    
    public PictureView(Picture picture, long likes, Boolean hasLiked) {
        this.id = picture.getId();
        this.text = picture.getText();
        this.content = picture.getContent();
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

    public byte[] getContent() {
        return content;
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
