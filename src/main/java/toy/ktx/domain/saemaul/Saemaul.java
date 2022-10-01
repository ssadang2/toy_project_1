package toy.ktx.domain.saemaul;

import toy.ktx.domain.Train;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "saemaul")
public class Saemaul extends Train {

    @OneToMany(mappedBy = "saemaul")
    private List<SaemaulRoom> saemaulRoomList = new ArrayList<>();
}
