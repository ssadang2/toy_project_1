package toy.ktx.repository.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.dto.api.DeployWithTrainDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static toy.ktx.domain.QDeploy.*;
import static toy.ktx.domain.QTrain.*;

@Repository
@Slf4j
//queryDsl를 써야 하고 보통의 Repo와는 수정의 라이프 사이클의 달라 분리했음
public class DeploySearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public DeploySearchRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    //출발 시간, 도착 시간을 조건으로 시간표 찾아오는 동적 쿼리
    public List<Deploy> searchDeploys(LocalDateTime goingTimeCond, LocalDateTime comingTimeCond) {
        return queryFactory.selectFrom(deploy)
                .where(dateTimeOfGoingGreaterThanEqual(goingTimeCond), dateTimeOfComingLessThanEqual(comingTimeCond))
                .fetch();
    }

    //출발 시간, 도착 시간을 조건으로 시간표 찾아오는 동적 쿼리
    //2023/1 기준 결과를 제대로 찾아오지 못하는 querydsl의 버그로 content를 가져오는 쿼리와 total query를 무조건 분리하여 날려야 됨
    public Page<DeployWithTrainDto> searchDeployDtos(LocalDateTime goingTimeCond, LocalDateTime comingTimeCond, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (goingTimeCond != null) {
            booleanBuilder.and(deploy.departureTime.goe(goingTimeCond));
        }

        if (comingTimeCond != null) {
            booleanBuilder.and(deploy.arrivalTime.loe(comingTimeCond));
        }

        List<DeployWithTrainDto> content = queryFactory
                .select(Projections.bean(DeployWithTrainDto.class,
                        deploy.id.as("deployId"),
                        deploy.departureTime,
                        deploy.arrivalTime,
                        deploy.departurePlace,
                        deploy.arrivalPlace,
                        deploy.train.id.as("trainId"),
                        deploy.train.trainName
                ))
                .from(deploy)
                .join(deploy.train, train)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //deploy가 없는 train은 존재할 수 없고 그 역도 마찬가지임(마치 버스와 버스 좌석의 관계와 비슷함) 따라서 fk를 안 가지고 있을 수 없다고 가정
        Long total = queryFactory.select(deploy.count())
                .from(deploy)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
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
            return deploy.arrivalTime.loe(comingTimeCond);
        }
    }

}
