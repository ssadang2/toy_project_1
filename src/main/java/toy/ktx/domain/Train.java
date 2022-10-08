package toy.ktx.domain;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "train")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Data
public class Train {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;

    @OneToMany(mappedBy = "train")
    private List<Deploy> deploys;

    public Train() {
    }

    public Train(String trainName) {
        this.trainName = trainName;
    }
}
