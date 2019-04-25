package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
        List<Comment> findByCommenterAndPicture(Account account, Picture picture);
        List<Comment> findByCommenterAndWall(Account account, Wall wall);
        List<Comment> findByPicture(Picture picture);   
        List<Comment> findByPicture(Picture picture, Pageable pageable);    
        List<Comment> findByWall(Wall wall);    
        List<Comment> findByWall(Wall wall, Pageable pageable);  
        Long countByWall(Wall wall);
}
