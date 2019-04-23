package projekti;

import java.time.LocalDateTime;

public class WallView {
    private long id;
    private String message; 
    private Account owner;
    private Account messager;       
    private LocalDateTime time;
    private long likes;
    private Boolean hasLiked;

    public WallView(long id, String message, Account owner, Account messager, LocalDateTime time, long likes, Boolean hasLiked) {
        this.id = id;
        this.message = message;
        this.owner = owner;
        this.messager = messager;
        this.time = time;
        this.likes = likes;
        this.hasLiked = hasLiked;
    }
    
    public WallView(Wall wall, long likes, Boolean hasLiked) {
        this.id = wall.getId();
        this.message = wall.getMessage();
        this.owner = wall.getOwner();
        this.messager = wall.getMessager();
        this.time = wall.getTime();
        this.likes = likes;
        this.hasLiked = hasLiked;
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


