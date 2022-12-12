package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;

import java.util.List;
import java.util.Optional;

public interface KtxRoomRepository extends JpaRepository<KtxRoom, Long> {

    List<KtxRoom> findByKtx(Ktx ktx);

    List<KtxRoom> findByKtxAndGrade(Ktx ktx, Grade grade);

//  이것도 데이터 채워 넣기 전에 양방향 없애면 에러 날 코드, 수정되어야 함
    @Query("select k from KtxRoom k join fetch k.ktx ktx join fetch ktx.deploy d where d.id = :id")
    List<KtxRoom> findKtxRoomWithTrainWithDeploy(@Param("id") Long id);

    @Query("select k from KtxRoom k join fetch k.ktxSeat where k.ktx.id= :id")
    List<KtxRoom> getKtxRoomsWithSeatFetch(@Param("id") Long id);
}
