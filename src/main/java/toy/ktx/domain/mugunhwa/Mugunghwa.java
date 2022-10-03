package toy.ktx.domain.mugunhwa;

import toy.ktx.domain.Train;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mugunghwa extends Train {

    @OneToMany(mappedBy = "mugunghwa")
    private List<MugunghwaRoom> mugunghwaRooms = new ArrayList<>();
}
