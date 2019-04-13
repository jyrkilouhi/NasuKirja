package projekti;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{
    Account findByUsername(String username);
    Account findByProfilename(String profilename);    
    Account findByRealname(String realname);    
    Account findByRealnameContaining(String realname);   
}
