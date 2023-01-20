package toy.ktx.domain.dto.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Map;

@Data
public class SaemaulSeatDto {

    private final Boolean s1A;
    private final Boolean s1B;
    private final Boolean s1C;
    private final Boolean s1D;

    private final Boolean s2A;
    private final Boolean s2B;
    private final Boolean s2C;
    private final Boolean s2D;

    private final Boolean s3A;
    private final Boolean s3B;
    private final Boolean s3C;
    private final Boolean s3D;

    private final Boolean s4A;
    private final Boolean s4B;
    private final Boolean s4C;
    private final Boolean s4D;

    private final Boolean s5A;
    private final Boolean s5B;
    private final Boolean s5C;
    private final Boolean s5D;

    private final Boolean s6A;
    private final Boolean s6B;
    private final Boolean s6C;
    private final Boolean s6D;

    private final Boolean s7A;
    private final Boolean s7B;
    private final Boolean s7C;
    private final Boolean s7D;

    private final Boolean s8A;
    private final Boolean s8B;
    private final Boolean s8C;
    private final Boolean s8D;

    private final Boolean s9A;
    private final Boolean s9B;
    private final Boolean s9C;
    private final Boolean s9D;

    private final Boolean s10A;
    private final Boolean s10B;
    private final Boolean s10C;
    private final Boolean s10D;

    private final Boolean s11A;
    private final Boolean s11B;
    private final Boolean s11C;
    private final Boolean s11D;

    private final Boolean s12A;
    private final Boolean s12B;
    private final Boolean s12C;
    private final Boolean s12D;

    private final Boolean s13A;
    private final Boolean s13B;
    private final Boolean s13C;
    private final Boolean s13D;

    private final Boolean s14A;
    private final Boolean s14B;
    private final Boolean s14C;
    private final Boolean s14D;

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
