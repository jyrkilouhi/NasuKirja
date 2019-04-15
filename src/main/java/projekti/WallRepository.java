package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallRepository extends JpaRepository<Picture, Long> {
        List<Wall> findByOwner(Account ownerAccount);
    
}
