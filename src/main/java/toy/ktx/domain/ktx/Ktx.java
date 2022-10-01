package toy.ktx.domain.ktx;

import toy.ktx.domain.Train;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "ktx")
public class Ktx extends Train {

    @OneToMany(mappedBy = "ktx")
    private List<KtxRoom> ktxRoomList = new ArrayList<>();

}
