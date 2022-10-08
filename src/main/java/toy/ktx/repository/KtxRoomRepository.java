package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.ktx.KtxRoom;

public interface KtxRoomRepository extends JpaRepository<KtxRoom, Long> {
}
