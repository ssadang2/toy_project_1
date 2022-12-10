package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;

import java.util.List;

public interface KtxRoomRepository extends JpaRepository<KtxRoom, Long> {

    List<KtxRoom> findByKtx(Ktx ktx);

    List<KtxRoom> findByKtxAndGrade(Ktx ktx, Grade grade);

    @Query("select k from KtxRoom k join fetch k.ktx ktx join fetch ktx.deploy d where d.id = :id")
    List<KtxRoom> findKtxRoomWithTrainWithDeploy(@Param("id") Long id);
}
