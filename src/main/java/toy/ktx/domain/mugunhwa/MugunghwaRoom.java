package toy.ktx.domain.mugunhwa;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;


@Entity
@Data
@Table(name = "mugunghwa_room")
public class MugunghwaRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mugunghwa_id")
    private Mugunghwa mugunghwa;

    @Enumerated(EnumType.STRING)
    private Grade grade;
}
