package toy.ktx.domain.mugunhwa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import toy.ktx.domain.dto.projections.KtxNormalSeatDto;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Entity
@Data
@Table(name = "mugunghwa_seat")
public class MugunghwaSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public MugunghwaSeat() {
    }

    public MugunghwaSeat(Boolean m1, Boolean m2, Boolean m3, Boolean m4, Boolean m5, Boolean m6, Boolean m7, Boolean m8, Boolean m9, Boolean m10, Boolean m11, Boolean m12, Boolean m13, Boolean m14, Boolean m15, Boolean m16, Boolean m17, Boolean m18, Boolean m19, Boolean m20, Boolean m21, Boolean m22, Boolean m23, Boolean m24, Boolean m25, Boolean m26, Boolean m27, Boolean m28, Boolean m29, Boolean m30, Boolean m31, Boolean m32, Boolean m33, Boolean m34, Boolean m35, Boolean m36, Boolean m37, Boolean m38, Boolean m39, Boolean m40, Boolean m41, Boolean m42, Boolean m43, Boolean m44, Boolean m45, Boolean m46, Boolean m47, Boolean m48, Boolean m49, Boolean m50, Boolean m51, Boolean m52, Boolean m53, Boolean m54, Boolean m55, Boolean m56, Boolean m57, Boolean m58, Boolean m59, Boolean m60, Boolean m61, Boolean m62, Boolean m63, Boolean m64, Boolean m65, Boolean m66, Boolean m67, Boolean m68, Boolean m69, Boolean m70, Boolean m71, Boolean m72) {
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.m4 = m4;
        this.m5 = m5;
        this.m6 = m6;
        this.m7 = m7;
        this.m8 = m8;
        this.m9 = m9;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        this.m15 = m15;
        this.m16 = m16;
        this.m17 = m17;
        this.m18 = m18;
        this.m19 = m19;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        this.m25 = m25;
        this.m26 = m26;
        this.m27 = m27;
        this.m28 = m28;
        this.m29 = m29;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        this.m35 = m35;
        this.m36 = m36;
        this.m37 = m37;
        this.m38 = m38;
        this.m39 = m39;
        this.m40 = m40;
        this.m41 = m41;
        this.m42 = m42;
        this.m43 = m43;
        this.m44 = m44;
        this.m45 = m45;
        this.m46 = m46;
        this.m47 = m47;
        this.m48 = m48;
        this.m49 = m49;
        this.m50 = m50;
        this.m51 = m51;
        this.m52 = m52;
        this.m53 = m53;
        this.m54 = m54;
        this.m55 = m55;
        this.m56 = m56;
        this.m57 = m57;
        this.m58 = m58;
        this.m59 = m59;
        this.m60 = m60;
        this.m61 = m61;
        this.m62 = m62;
        this.m63 = m63;
        this.m64 = m64;
        this.m65 = m65;
        this.m66 = m66;
        this.m67 = m67;
        this.m68 = m68;
        this.m69 = m69;
        this.m70 = m70;
        this.m71 = m71;
        this.m72 = m72;
    }

    public Boolean remain(Integer passengers) {
        ObjectMapper objectMapper = new ObjectMapper();
        Long remain = Long.valueOf(0);
        Map map = objectMapper.convertValue(this, Map.class);
        for (Object o : map.keySet()) {
            if (map.get(o).equals(Boolean.FALSE)) {
                remain += 1;
            }
        }
        if (remain >= passengers) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void mugunghwaDtoToEntity(MugunghwaSeatDto mugunghwaSeatDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
        try {
            Class clazz = Class.forName("toy.ktx.domain.mugunhwa.MugunghwaSeat");
            for (Object o : seatMap.keySet()) {
                if ((boolean) seatMap.get(o) == true) {
                    String temp = "setM" + ((String)o).substring(1);
                    Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
                    declaredMethod.invoke(this, true);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkSeats(String beforeChosenSeats) {
        String[] split = beforeChosenSeats.split(" ");
        try {
            Class clazz = Class.forName("toy.ktx.domain.mugunhwa.MugunghwaSeat");
            for (String s : split) {
                String seat = "setM" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(seat, Boolean.class);
                declaredMethod.invoke(this, true);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
