package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;

import java.util.List;
import java.util.Optional;

public interface KtxRoomRepository extends JpaRepository<KtxRoom, Long> {

    List<KtxRoom> findByKtx(Ktx ktx);

    List<KtxRoom> findByKtxAndGrade(Ktx ktx, Grade grade);
}
