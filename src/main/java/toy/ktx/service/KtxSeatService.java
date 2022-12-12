package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.repository.KtxSeatRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatService {

    private final KtxSeatRepository ktxSeatRepository;

    @Transactional
    public void saveKtxSeat(KtxSeat ktxSeat) {
        ktxSeatRepository.save(ktxSeat);
    }

    public Optional<KtxSeat> findKtxSeat(Long ktxSeatId) {
        return ktxSeatRepository.findById(ktxSeatId);
    }

//    public Optional<KtxSeat> findByKtxRoom(KtxRoom ktxRoom) {
//        return ktxSeatRepository.findByKtxRoom(ktxRoom);
//    }

//    public NormalSeatDto findNormalDtoByKtxRoom(KtxRoom ktxRoom) {
//        return ktxSeatRepository.findNormalDtoByKtxRoom(ktxRoom);
//    }

//    public VipSeatDto findVipDtoByKtxRoom(KtxRoom ktxRoom) {
//        return ktxSeatRepository.findVipDtoByKtxRoom(ktxRoom);
//    }

    @Transactional
    public void updateSeatsWithReflection(KtxSeat ktxSeat, String seats) {
        try {
            Class clazz;
            if (ktxSeat instanceof KtxSeatNormal) {
                clazz = Class.forName("toy.ktx.domain.ktx.KtxSeatNormal");
            }
            else {
                clazz = Class.forName("toy.ktx.domain.ktx.KtxSeatVip");
            }
            String[] seatsArr = seats.split(" ");
            for (String s : seatsArr) {
                String temp = "setK" + s.substring(1);
                Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
                declaredMethod.invoke(ktxSeat, false);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    public List<KtxSeat> findKtxSeatWithKtxRoomWithTrainWithDeploy(Long id) {
//        return ktxSeatRepository.findKtxSeatWithKtxRoomWithTrainWithDeploy(id);
//    }
}
