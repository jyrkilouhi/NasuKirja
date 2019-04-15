package projekti;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture extends AbstractPersistable<Long> {
    
    private String kuvaus; 
    
    @Lob
    private byte[] content;
    
    @OneToOne
    private Account owner;
    
}
