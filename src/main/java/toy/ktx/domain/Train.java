package toy.ktx.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "train")
@DiscriminatorColumn
@Data
@ToString(exclude = "deploys")
//toString stackOverFlow 막으려고
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
