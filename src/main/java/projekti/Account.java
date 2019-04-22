package projekti;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    @NotEmpty
    @Size(min = 5, max = 50)
    private String username;

    @NotEmpty
    @Size(min = 8, max = 150)
    private String password; 

    @NotEmpty
    @Size(min = 5, max = 50)    
    private String realname;

    @NotEmpty
    @Size(min = 5, max = 50)    
    private String profilename;
    
    @OneToOne
    private Picture profilePicture;
    
}