package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.SaemaulSeatDto;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.repository.SaemaulSeatRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaemaulSeatService {

    private final SaemaulSeatRepository saemaulSeatRepository;

    public SaemaulSeatDto findSaemaulSeatDtoById(Long id) {
        return saemaulSeatRepository.findSaemaulSeatDtoById(id);
    }

    @Transactional
    public void save(SaemaulSeat saemaulSeat) {
        saemaulSeatRepository.save(saemaulSeat);
    }

    @Transactional
    public void updateSeatsWithReflection(SaemaulSeat saemaulSeat, String seats) {
        try {
            Class clazz = Class.forName("toy.ktx.domain.saemaul.SaemaulSeat");

            String[] seatsArr = seats.split(" ");
            for (String s : seatsArr) {
                String temp = "setS" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
                declaredMethod.invoke(saemaulSeat, false);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
