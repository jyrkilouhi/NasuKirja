package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FriendsRepository extends JpaRepository<Friend, Long>{
    Friend findByAskedbyAndAskedfrom(Account account1, Account account2); 
    List<Friend> findByAskedfromAndStatus(Account account, Boolean status);
    List<Friend> findByAskedbyAndStatus(Account account, Boolean status);
}
