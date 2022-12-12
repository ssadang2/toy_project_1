package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.ktx.Ktx;

public interface KtxRepository extends JpaRepository<Ktx, Long> {

}
