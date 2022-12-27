package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.repository.MugunghwaSeatRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MugunghwaSeatService {

    private final MugunghwaSeatRepository mugunghwaSeatRepository;

    public MugunghwaSeatDto findMugunghwaSeatDtoById(Long id) {
        return mugunghwaSeatRepository.findMugunghwaSeatDtoById(id);
    }

    @Transactional
    public void save(MugunghwaSeat mugunghwaSeat) {
        mugunghwaSeatRepository.save(mugunghwaSeat);
    }

    @Transactional
    public void updateSeatsWithReflection(MugunghwaSeat mugunghwaSeat, String seats) {
        try {
            Class clazz = Class.forName("toy.ktx.domain.mugunhwa.MugunghwaSeat");

            String[] seatsArr = seats.split(" ");
            for (String s : seatsArr) {
                String temp = "setM" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
                declaredMethod.invoke(mugunghwaSeat, false);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
