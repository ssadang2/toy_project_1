package toy.ktx.domain;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "train")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Data
public class Train {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deploy_id")
    private Deploy deploy;

}
