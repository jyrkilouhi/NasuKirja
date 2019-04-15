package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByOwner(Account ownerAccount);
    
}
