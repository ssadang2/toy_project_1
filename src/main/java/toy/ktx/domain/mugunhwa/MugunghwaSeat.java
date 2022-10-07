package toy.ktx.domain.mugunhwa;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "mugunghwa_seat")
public class MugunghwaSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private MugunghwaRoom mugunghwaRoom;

    private Boolean m1;
    private Boolean m2;
    private Boolean m3;
    private Boolean m4;
    private Boolean m5;
    private Boolean m6;
    private Boolean m7;
    private Boolean m8;
    private Boolean m9;
    private Boolean m10;
    private Boolean m11;
    private Boolean m12;
    private Boolean m13;
    private Boolean m14;
    private Boolean m15;
    private Boolean m16;
    private Boolean m17;
    private Boolean m18;

    private Boolean m19;
    private Boolean m20;
    private Boolean m21;
    private Boolean m22;
    private Boolean m23;
    private Boolean m24;
    private Boolean m25;
    private Boolean m26;
    private Boolean m27;
    private Boolean m28;
    private Boolean m29;
    private Boolean m30;
    private Boolean m31;
    private Boolean m32;
    private Boolean m33;
    private Boolean m34;
    private Boolean m35;
    private Boolean m36;

    private Boolean m37;
    private Boolean m38;
    private Boolean m39;
    private Boolean m40;
    private Boolean m41;
    private Boolean m42;
    private Boolean m43;
    private Boolean m44;
    private Boolean m45;
    private Boolean m46;
    private Boolean m47;
    private Boolean m48;
    private Boolean m49;
    private Boolean m50;
    private Boolean m51;
    private Boolean m52;
    private Boolean m53;
    private Boolean m54;
    private Boolean m55;

    private Boolean m56;
    private Boolean m57;
    private Boolean m58;
    private Boolean m59;
    private Boolean m60;
    private Boolean m61;
    private Boolean m62;
    private Boolean m63;
    private Boolean m64;
    private Boolean m65;
    private Boolean m66;
    private Boolean m67;
    private Boolean m68;
    private Boolean m69;
    private Boolean m70;
    private Boolean m71;
    private Boolean m72;
}
