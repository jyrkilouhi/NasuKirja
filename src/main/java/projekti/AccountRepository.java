package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{
    Account findByUsername(String username);
    Account findByProfilename(String profilename);      
    List<Account> findByRealnameContaining(String realname);   
}
