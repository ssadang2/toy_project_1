package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;

import java.util.List;

public interface SaemaulRoomRepository extends JpaRepository<SaemaulRoom, Long> {

    @Query("select s from SaemaulRoom s join fetch s.saemaulSeat where s.saemaul.id= :id")
    List<SaemaulRoom> getSaemaulRoomsToSeatByIdWithFetch(@Param("id") Long id);

    List<SaemaulRoom> findAllBySaemaul(Saemaul saemaul);
}
