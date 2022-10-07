package toy.ktx.domain.ktx;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ktx_room")
public class KtxRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ktx_id")
    private Ktx ktx;

    @Enumerated(EnumType.STRING)
    private Grade grade;

}
