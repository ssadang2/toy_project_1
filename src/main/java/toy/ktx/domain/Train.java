package toy.ktx.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "train")
@DiscriminatorColumn
@Data
@ToString(exclude = "deploy")
//toString stackOverFlow 막으려고
//아니면 @data를 빼버려도 될 듯
public class Train {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;

    @OneToOne(mappedBy = "train", fetch = FetchType.LAZY)
    private Deploy deploy;

    public Train() {
    }

    public Train(String trainName) {
        this.trainName = trainName;
    }
}
