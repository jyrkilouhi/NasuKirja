package projekti;

import java.time.LocalDateTime;
import java.util.List;

public class WallView {
    private long id;
    private String message; 
    private Account owner;
    private Account messager;       
    private LocalDateTime time;
    private long likes;
    private Boolean hasLiked;
    private List<Comment> comments;

    public WallView(long id, String message, Account owner, Account messager, LocalDateTime time, long likes, Boolean hasLiked, List<Comment> comments) {
        this.id = id;
        this.message = message;
        this.owner = owner;
        this.messager = messager;
        this.time = time;
        this.likes = likes;
        this.hasLiked = hasLiked;
        this.comments = comments;
    }
    
    public WallView(Wall wall, long likes, Boolean hasLiked, List<Comment> comments) {
        this.id = wall.getId();
        this.message = wall.getMessage();
        this.owner = wall.getOwner();
        this.messager = wall.getMessager();
        this.time = wall.getTime();
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

    public String getMessage() {
        return message;
    }

    public Account getOwner() {
        return owner;
    }

    public Account getMessager() {
        return messager;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public long getLikes() {
        return likes;
    }

    public Boolean getHasLiked() {
        return hasLiked;
    }   
    
}


