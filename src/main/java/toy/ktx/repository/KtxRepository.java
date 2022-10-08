package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.ktx.Ktx;

public interface KtxRepository extends JpaRepository<Ktx, Long> {
}
