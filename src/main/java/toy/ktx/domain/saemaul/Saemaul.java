package toy.ktx.domain.saemaul;

import lombok.Getter;
import lombok.Setter;
import toy.ktx.domain.Train;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Saemaul extends Train {

    @OneToMany(mappedBy = "saemaul", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SaemaulRoom> saemaulRooms = new ArrayList<>();

    public Saemaul() {

    }

    public Saemaul(String trainName) {
        super(trainName);
    }
}

