package toy.ktx.domain.mugunhwa;

import toy.ktx.domain.Train;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "mugunghwa")
public class Mugunghwa extends Train {

    @OneToMany(mappedBy = "mugunghwa")
    private List<MugunghwaRoom> mugunhwaRoomList = new ArrayList<>();
}
