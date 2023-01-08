package toy.ktx.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import toy.ktx.domain.Deploy;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static toy.ktx.domain.QDeploy.*;

@Repository
public class DeploySearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public DeploySearchRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Deploy> searchDeploys(LocalDateTime goingTimeCond, LocalDateTime comingTimeCond) {
        return queryFactory.selectFrom(deploy)
                .where(dateTimeOfGoingGreaterThanEqual(goingTimeCond), dateTimeOfComingLessThanEqual(comingTimeCond))
                .fetch();
    }

    public BooleanExpression dateTimeOfGoingGreaterThanEqual(LocalDateTime goingTimeCond) {
        if (goingTimeCond == null) {
            return null;
        } else {
            return deploy.departureTime.goe(goingTimeCond);
        }
    }

    public BooleanExpression dateTimeOfComingLessThanEqual(LocalDateTime comingTimeCond) {
        if (comingTimeCond == null) {
            return null;
        } else {
            return deploy.departureTime.loe(comingTimeCond);
        }
    }

}
