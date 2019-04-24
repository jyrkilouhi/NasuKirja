package projekti;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Love extends AbstractPersistable<Long> {
    
    @OneToOne
    private Picture picture;
    
    @OneToOne
    private Wall wall;
    
    @OneToOne
    private Account lover;
     
}
