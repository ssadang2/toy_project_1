package toy.ktx.domain.dto.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class NormalSeatDto {

    private final Boolean k1A;
    private final Boolean k1B;
    private final Boolean k1C;
    private final Boolean k1D;

    private final Boolean k2A;
    private final Boolean k2B;
    private final Boolean k2C;
    private final Boolean k2D;

    private final Boolean k3A;
    private final Boolean k3B;
    private final Boolean k3C;
    private final Boolean k3D;

    private final Boolean k4A;
    private final Boolean k4B;
    private final Boolean k4C;
    private final Boolean k4D;

    private final Boolean k5A;
    private final Boolean k5B;
    private final Boolean k5C;
    private final Boolean k5D;

    private final Boolean k6A;
    private final Boolean k6B;
    private final Boolean k6C;
    private final Boolean k6D;

    private final Boolean k7A;
    private final Boolean k7B;
    private final Boolean k7C;
    private final Boolean k7D;

    private final Boolean k8A;
    private final Boolean k8B;
    private final Boolean k8C;
    private final Boolean k8D;

    private final Boolean k9A;
    private final Boolean k9B;
    private final Boolean k9C;
    private final Boolean k9D;

    private final Boolean k10A;
    private final Boolean k10B;
    private final Boolean k10C;
    private final Boolean k10D;

    private final Boolean k11A;
    private final Boolean k11B;
    private final Boolean k11C;
    private final Boolean k11D;

    private final Boolean k12A;
    private final Boolean k12B;
    private final Boolean k12C;
    private final Boolean k12D;

    private final Boolean k13A;
    private final Boolean k13B;
    private final Boolean k13C;
    private final Boolean k13D;

    private final Boolean k14A;
    private final Boolean k14B;
    private final Boolean k14C;
    private final Boolean k14D;

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
