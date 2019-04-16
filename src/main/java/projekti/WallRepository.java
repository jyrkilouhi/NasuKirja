package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallRepository extends JpaRepository<Wall, Long> {
        List<Wall> findByOwner(Account ownerAccount);
        List<Wall> findByOwner(Account ownerAccount, Pageable pageable);    
}
