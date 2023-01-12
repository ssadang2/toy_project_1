package toy.ktx.domain.mugunhwa;

import lombok.Getter;
import lombok.Setter;
import toy.ktx.domain.Train;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Mugunghwa extends Train {

    @OneToMany(mappedBy = "mugunghwa", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MugunghwaRoom> mugunghwaRooms = new ArrayList<>();

    public Mugunghwa() {
    }

    public Mugunghwa(String trainName) {
        super(trainName);
    }
}
