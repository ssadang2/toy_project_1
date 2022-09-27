package toy.ktx.domain.ktx;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;

@Entity
@Data
public class KtxRoom {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ktx_id")
    private Ktx ktx;

    @Enumerated(EnumType.STRING)
    private Grade grade;

}
