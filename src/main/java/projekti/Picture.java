package projekti;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    
    private String text; 
    
    // TODO - Miten pitäisi tehdä ratkaisu jossa H2 ympäristössä on @Lob ja Herokussa ei
    //@Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;
    
    @OneToOne
    private Account owner;
    
}
