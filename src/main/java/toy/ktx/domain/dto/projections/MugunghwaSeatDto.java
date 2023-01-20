package toy.ktx.domain.dto.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Map;

@Data
public class MugunghwaSeatDto {

    private final Boolean m1;
    private final Boolean m2;
    private final Boolean m3;
    private final Boolean m4;
    private final Boolean m5;
    private final Boolean m6;
    private final Boolean m7;
    private final Boolean m8;
    private final Boolean m9;
    private final Boolean m10;
    private final Boolean m11;
    private final Boolean m12;
    private final Boolean m13;
    private final Boolean m14;
    private final Boolean m15;
    private final Boolean m16;
    private final Boolean m17;
    private final Boolean m18;

    private final Boolean m19;
    private final Boolean m20;
    private final Boolean m21;
    private final Boolean m22;
    private final Boolean m23;
    private final Boolean m24;
    private final Boolean m25;
    private final Boolean m26;
    private final Boolean m27;
    private final Boolean m28;
    private final Boolean m29;
    private final Boolean m30;
    private final Boolean m31;
    private final Boolean m32;
    private final Boolean m33;
    private final Boolean m34;
    private final Boolean m35;
    private final Boolean m36;

    private final Boolean m37;
    private final Boolean m38;
    private final Boolean m39;
    private final Boolean m40;
    private final Boolean m41;
    private final Boolean m42;
    private final Boolean m43;
    private final Boolean m44;
    private final Boolean m45;
    private final Boolean m46;
    private final Boolean m47;
    private final Boolean m48;
    private final Boolean m49;
    private final Boolean m50;
    private final Boolean m51;
    private final Boolean m52;
    private final Boolean m53;
    private final Boolean m54;
    private final Boolean m55;

    private final Boolean m56;
    private final Boolean m57;
    private final Boolean m58;
    private final Boolean m59;
    private final Boolean m60;
    private final Boolean m61;
    private final Boolean m62;
    private final Boolean m63;
    private final Boolean m64;
    private final Boolean m65;
    private final Boolean m66;
    private final Boolean m67;
    private final Boolean m68;
    private final Boolean m69;
    private final Boolean m70;
    private final Boolean m71;
    private final Boolean m72;

    public Integer howManyOccupied() {
        Integer sum = 0;

        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.convertValue(this, Map.class);

        for (Object key : map.keySet()) {
            if(!Boolean.FALSE.equals((Boolean) map.get(key))) {
                sum += 1;
            }
        }
        return sum;
    }

    public String returnSeats() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.convertValue(this, Map.class);
        String seats = "";

        for (Object key : map.keySet()) {
            if (((Boolean) map.get(key)).equals(Boolean.TRUE)) {
                seats += (String) key + " ";;
            }
        }
        seats = seats.substring(0, seats.length() - 1);
        return seats;
    }
}
