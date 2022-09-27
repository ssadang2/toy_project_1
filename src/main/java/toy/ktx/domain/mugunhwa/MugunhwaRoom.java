package toy.ktx.domain.mugunhwa;

import lombok.Data;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;

import javax.persistence.*;


@Entity
@Data
public class MugunhwaRoom {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mugunhwa_id")
    private Mugunhwa mugunhwa;

    @Enumerated(EnumType.STRING)
    private Grade grade;
}
