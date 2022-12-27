package toy.ktx.domain.saemaul;

import lombok.Getter;
import toy.ktx.domain.Train;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Saemaul extends Train {

    @OneToMany(mappedBy = "saemaul")
    private List<SaemaulRoom> saemaulRooms = new ArrayList<>();

    public Saemaul() {

    }

    public Saemaul(String trainName) {
        super(trainName);
    }
}

