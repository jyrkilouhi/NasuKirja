package projekti;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WallView {
    private long id;
    private String message; 
    private Account owner;
    private Account messager;       
    private LocalDateTime time;
    private long likes;
    private Boolean hasLiked;
    private List<Comment> comments;
    
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
}


