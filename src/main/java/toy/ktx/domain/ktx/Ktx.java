package toy.ktx.domain.ktx;

import lombok.Getter;
import lombok.Setter;
import toy.ktx.domain.Train;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Ktx extends Train {

    @OneToMany(mappedBy = "ktx", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KtxRoom> ktxRooms = new ArrayList<>();

    public Ktx() {
    }

    public Ktx(String trainName) {
        super(trainName);
    }
}
