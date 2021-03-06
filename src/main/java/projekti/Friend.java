package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class Friend extends AbstractPersistable<Long> {
    
    private LocalTime asktime;
    private LocalDate askdate;
    
    @OneToOne
    private Account askedby;
    
    @OneToOne
    private Account askedfrom;
    
    private boolean status;
    
}
