package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoveRepository extends JpaRepository<Love, Long> {
        List<Love> findByLoverAndPicture(Account loverAccount, Picture picture);
        List<Love> findByLoverAndWall(Account loverAccount, Wall wall);
        List<Love> findByLover(Account loverAccount);
        List<Love> findByPicture(Picture picture);    
        List<Love> findByWall(Wall wall);    
        Long countByWall(Wall wall);
        Long countByPicture(Picture picture);
}
