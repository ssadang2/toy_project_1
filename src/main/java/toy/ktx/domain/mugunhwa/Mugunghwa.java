package toy.ktx.domain.mugunhwa;

import lombok.Getter;
import toy.ktx.domain.Train;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Mugunghwa extends Train {

    @OneToMany(mappedBy = "mugunghwa", orphanRemoval = true)
    private List<MugunghwaRoom> mugunghwaRooms = new ArrayList<>();

    public Mugunghwa() {
    }

    public Mugunghwa(String trainName) {
        super(trainName);
    }
}
