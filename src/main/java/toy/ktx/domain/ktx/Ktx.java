package toy.ktx.domain.ktx;

import lombok.Getter;
import toy.ktx.domain.Train;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Ktx extends Train {

    @OneToMany(mappedBy = "ktx", orphanRemoval = true)
    private List<KtxRoom> ktxRooms = new ArrayList<>();

    public Ktx() {
    }

    public Ktx(String trainName) {
        super(trainName);
    }
}
