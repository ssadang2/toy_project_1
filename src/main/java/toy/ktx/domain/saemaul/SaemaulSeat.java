package toy.ktx.domain.saemaul;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;
import toy.ktx.domain.dto.projections.SaemaulSeatDto;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Entity
@Data
@Table(name = "saemaul_seat")
public class SaemaulSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean s1A;
    private Boolean s2A;
    private Boolean s3A;
    private Boolean s4A;
    private Boolean s5A;
    private Boolean s6A;
    private Boolean s7A;
    private Boolean s8A;
    private Boolean s9A;
    private Boolean s10A;
    private Boolean s11A;
    private Boolean s12A;
    private Boolean s13A;
    private Boolean s14A;

    private Boolean s1B;
    private Boolean s2B;
    private Boolean s3B;
    private Boolean s4B;
    private Boolean s5B;
    private Boolean s6B;
    private Boolean s7B;
    private Boolean s8B;
    private Boolean s9B;
    private Boolean s10B;
    private Boolean s11B;
    private Boolean s12B;
    private Boolean s13B;
    private Boolean s14B;

    private Boolean s1C;
    private Boolean s2C;
    private Boolean s3C;
    private Boolean s4C;
    private Boolean s5C;
    private Boolean s6C;
    private Boolean s7C;
    private Boolean s8C;
    private Boolean s9C;
    private Boolean s10C;
    private Boolean s11C;
    private Boolean s12C;
    private Boolean s13C;
    private Boolean s14C;

    private Boolean s1D;
    private Boolean s2D;
    private Boolean s3D;
    private Boolean s4D;
    private Boolean s5D;
    private Boolean s6D;
    private Boolean s7D;
    private Boolean s8D;
    private Boolean s9D;
    private Boolean s10D;
    private Boolean s11D;
    private Boolean s12D;
    private Boolean s13D;
    private Boolean s14D;

    public SaemaulSeat() {
    }

    public SaemaulSeat(Boolean s1A, Boolean s2A, Boolean s3A, Boolean s4A, Boolean s5A, Boolean s6A, Boolean s7A, Boolean s8A, Boolean s9A, Boolean s10A, Boolean s11A, Boolean s12A, Boolean s13A, Boolean s14A, Boolean s1B, Boolean s2B, Boolean s3B, Boolean s4B, Boolean s5B, Boolean s6B, Boolean s7B, Boolean s8B, Boolean s9B, Boolean s10B, Boolean s11B, Boolean s12B, Boolean s13B, Boolean s14B, Boolean s1C, Boolean s2C, Boolean s3C, Boolean s4C, Boolean s5C, Boolean s6C, Boolean s7C, Boolean s8C, Boolean s9C, Boolean s10C, Boolean s11C, Boolean s12C, Boolean s13C, Boolean s14C, Boolean s1D, Boolean s2D, Boolean s3D, Boolean s4D, Boolean s5D, Boolean s6D, Boolean s7D, Boolean s8D, Boolean s9D, Boolean s10D, Boolean s11D, Boolean s12D, Boolean s13D, Boolean s14D) {
        this.s1A = s1A;
        this.s2A = s2A;
        this.s3A = s3A;
        this.s4A = s4A;
        this.s5A = s5A;
        this.s6A = s6A;
        this.s7A = s7A;
        this.s8A = s8A;
        this.s9A = s9A;
        this.s10A = s10A;
        this.s11A = s11A;
        this.s12A = s12A;
        this.s13A = s13A;
        this.s14A = s14A;
        this.s1B = s1B;
        this.s2B = s2B;
        this.s3B = s3B;
        this.s4B = s4B;
        this.s5B = s5B;
        this.s6B = s6B;
        this.s7B = s7B;
        this.s8B = s8B;
        this.s9B = s9B;
        this.s10B = s10B;
        this.s11B = s11B;
        this.s12B = s12B;
        this.s13B = s13B;
        this.s14B = s14B;
        this.s1C = s1C;
        this.s2C = s2C;
        this.s3C = s3C;
        this.s4C = s4C;
        this.s5C = s5C;
        this.s6C = s6C;
        this.s7C = s7C;
        this.s8C = s8C;
        this.s9C = s9C;
        this.s10C = s10C;
        this.s11C = s11C;
        this.s12C = s12C;
        this.s13C = s13C;
        this.s14C = s14C;
        this.s1D = s1D;
        this.s2D = s2D;
        this.s3D = s3D;
        this.s4D = s4D;
        this.s5D = s5D;
        this.s6D = s6D;
        this.s7D = s7D;
        this.s8D = s8D;
        this.s9D = s9D;
        this.s10D = s10D;
        this.s11D = s11D;
        this.s12D = s12D;
        this.s13D = s13D;
        this.s14D = s14D;
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

    public void saemaulDtoToEntity(SaemaulSeatDto saemaulSeatDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
        try {
            Class clazz = Class.forName("toy.ktx.domain.saemaul.SaemaulSeat");
            for (Object o : seatMap.keySet()) {
                if ((boolean) seatMap.get(o) == true) {
                    String temp = "setS" + ((String)o).substring(1);
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
            Class clazz = Class.forName("toy.ktx.domain.saemaul.SaemaulSeat");
            for (String s : split) {
                String seat = "setS" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(seat, Boolean.class);
                declaredMethod.invoke(this, true);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
