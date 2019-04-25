package projekti;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
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

    @Size(min = 4, max = 50, message = "Käyttäjänimen pituus vähintään 4 merkkiä, maksimi 50 merkkiä")
    private String username;

    @Size(min = 8, max = 150, message = "Salasanan pituus vähintään 8 merkkiä, maksimi 150 merkkiä")
    private String password; 

    @Size(min = 4, max = 50, message = "Nimen pituus vähintään 4 merkkiä, maksimi 50 merkkiä")    
    private String realname;

    @Pattern(regexp = "[a-z0-9+-_]+", message = "Profiilinimi voi sisältää vain merkkejä a-z, 0-9 ja +-_ (ei välilyöntiä, ei Isoja kirjaimia, ei &?/#! yms merkkejä)")
    @Size(min = 4, max = 50, message = "Profiilinimen pituus 4-50 merkkiä")    
    private String profilename;
    
    @OneToOne
    private Picture profilePicture;
    
}