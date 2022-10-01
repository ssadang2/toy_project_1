package toy.ktx.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "train")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Data
public class Train {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deploy_id")
    private Deploy deploy;

}
