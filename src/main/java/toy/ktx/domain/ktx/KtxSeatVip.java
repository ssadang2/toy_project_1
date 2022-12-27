package toy.ktx.domain.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.dto.projections.KtxVipSeatDto;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Entity
@Data
@Slf4j
@Table(name = "ktx_seat_vip")
public class KtxSeatVip extends KtxSeat{

    private  Boolean k1A;
    private  Boolean k1B;
    private  Boolean k1C;

    private  Boolean k2A;
    private  Boolean k2B;
    private  Boolean k2C;

    private  Boolean k3A;
    private  Boolean k3B;
    private  Boolean k3C;

    private  Boolean k4A;
    private  Boolean k4B;
    private  Boolean k4C;

    private  Boolean k5A;
    private  Boolean k5B;
    private  Boolean k5C;

    private  Boolean k6A;
    private  Boolean k6B;
    private  Boolean k6C;

    private  Boolean k7A;
    private  Boolean k7B;
    private  Boolean k7C;

    private  Boolean k8A;
    private  Boolean k8B;
    private  Boolean k8C;

    private  Boolean k9A;
    private  Boolean k9B;
    private  Boolean k9C;

    private  Boolean k10A;
    private  Boolean k10B;
    private  Boolean k10C;

    private  Boolean k11A;
    private  Boolean k11B;
    private  Boolean k11C;

    private  Boolean k12A;
    private  Boolean k12B;
    private  Boolean k12C;

    private  Boolean k13A;
    private  Boolean k13B;
    private  Boolean k13C;

    private  Boolean k14A;
    private  Boolean k14B;
    private  Boolean k14C;

    public KtxSeatVip() {
    }

    public KtxSeatVip(Boolean k1A, Boolean k1B, Boolean k1C, Boolean k2A, Boolean k2B, Boolean k2C, Boolean k3A, Boolean k3B, Boolean k3C, Boolean k4A, Boolean k4B, Boolean k4C, Boolean k5A, Boolean k5B, Boolean k5C, Boolean k6A, Boolean k6B, Boolean k6C, Boolean k7A, Boolean k7B, Boolean k7C, Boolean k8A, Boolean k8B, Boolean k8C, Boolean k9A, Boolean k9B, Boolean k9C, Boolean k10A, Boolean k10B, Boolean k10C, Boolean k11A, Boolean k11B, Boolean k11C, Boolean k12A, Boolean k12B, Boolean k12C, Boolean k13A, Boolean k13B, Boolean k13C, Boolean k14A, Boolean k14B, Boolean k14C) {
        this.k1A = k1A;
        this.k1B = k1B;
        this.k1C = k1C;
        this.k2A = k2A;
        this.k2B = k2B;
        this.k2C = k2C;
        this.k3A = k3A;
        this.k3B = k3B;
        this.k3C = k3C;
        this.k4A = k4A;
        this.k4B = k4B;
        this.k4C = k4C;
        this.k5A = k5A;
        this.k5B = k5B;
        this.k5C = k5C;
        this.k6A = k6A;
        this.k6B = k6B;
        this.k6C = k6C;
        this.k7A = k7A;
        this.k7B = k7B;
        this.k7C = k7C;
        this.k8A = k8A;
        this.k8B = k8B;
        this.k8C = k8C;
        this.k9A = k9A;
        this.k9B = k9B;
        this.k9C = k9C;
        this.k10A = k10A;
        this.k10B = k10B;
        this.k10C = k10C;
        this.k11A = k11A;
        this.k11B = k11B;
        this.k11C = k11C;
        this.k12A = k12A;
        this.k12B = k12B;
        this.k12C = k12C;
        this.k13A = k13A;
        this.k13B = k13B;
        this.k13C = k13C;
        this.k14A = k14A;
        this.k14B = k14B;
        this.k14C = k14C;
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

    public void vipDtoToEntity(KtxVipSeatDto ktxVipSeatDto) {
        Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
        try {
            Class clazz = Class.forName("toy.ktx.domain.ktx.KtxSeatVip");
            for (Object o : seatMap.keySet()) {
                if ((boolean) seatMap.get(o) == true) {
                    String temp = "setK" + ((String)o).substring(1);
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
        log.info("fuck = {}", split);
        try {
            Class clazz = Class.forName("toy.ktx.domain.ktx.KtxSeatVip");
            for (String s : split) {
                String seat = "setK" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(seat, Boolean.class);
                declaredMethod.invoke(this, true);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
