package toy.ktx.domain.saemaul;

import lombok.Data;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.mugunhwa.Mugunhwa;

import javax.persistence.*;

@Entity
@Data
public class SaemaulRoom {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "saemaul_id")
    private Saemaul saemaul;

    @Enumerated(EnumType.STRING)
    private Grade grade;
}
