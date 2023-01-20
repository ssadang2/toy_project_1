package toy.ktx.repository.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.api.DeployWithTrainDto;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class DeploySearchRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private DeploySearchRepository deploySearchRepository;

    @Test
    void searchDeploys() {
        //given
        LocalDateTime now = LocalDateTime.now();

        Deploy deploy1 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Mugunghwa("mugunghwa1"));
        Deploy deploy2 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Mugunghwa("mugunghwa2"));
        Deploy deploy3 = new Deploy(now.plusHours(2), now.plusHours(4), "서울역", "부산역", new Saemaul("saemaul3"));
        Deploy deploy4 = new Deploy(now.plusHours(3), now.plusHours(5), "서울역", "부산역", new Saemaul("saemaul4"));
        Deploy deploy5 = new Deploy(now.plusHours(4), now.plusHours(6), "서울역", "부산역", new Ktx("ktx5"));
        Deploy deploy6 = new Deploy(now.plusHours(5), now.plusHours(7), "서울역", "부산역", new Ktx("ktx6"));

        em.persist(deploy1);
        em.persist(deploy2);
        em.persist(deploy3);
        em.persist(deploy4);
        em.persist(deploy5);
        em.persist(deploy6);

        //when
        //근데 도착 시간에 조건을 주어 deploy 1~5개까지 나오게 함
        List<Deploy> deploys = deploySearchRepository.searchDeploys(now, now.plusHours(6));

        //then
        assertThat(deploys).containsExactly(deploy1, deploy2, deploy3, deploy4, deploy5);
    }

    @Test
    void searchDeployDtosPaging() {
        //given
        LocalDateTime now = LocalDateTime.now();

        Deploy deploy1 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Mugunghwa("mugunghwa1"));
        Deploy deploy2 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Mugunghwa("mugunghwa2"));
        Deploy deploy3 = new Deploy(now.plusHours(2), now.plusHours(4), "서울역", "부산역", new Saemaul("saemaul3"));
        Deploy deploy4 = new Deploy(now.plusHours(3), now.plusHours(5), "서울역", "부산역", new Saemaul("saemaul4"));
        Deploy deploy5 = new Deploy(now.plusHours(4), now.plusHours(6), "서울역", "부산역", new Ktx("ktx5"));
        Deploy deploy6 = new Deploy(now.plusHours(5), now.plusHours(7), "서울역", "부산역", new Ktx("ktx6"));

        em.persist(deploy1);
        em.persist(deploy2);
        em.persist(deploy3);
        em.persist(deploy4);
        em.persist(deploy5);
        em.persist(deploy6);

        //6개의 데이터를 넣었을 때 1페이지 3개의 데이터를 요청 -> 1,2,3 거르고 4,5,6이 튀어나와야 함
        PageRequest of = PageRequest.of(1, 3);

        //when
        //모든 데이터 긁어 오기
        Page<DeployWithTrainDto> dtos = deploySearchRepository.searchDeployDtos(now, null, of);

        //then
        //search query의 return type은 Dto고 db에 있는 값은 deploy entity이기 때문에 비교를 위해 List<Dto>를 id를 갖는 list로 map해줘야 됨
        List<DeployWithTrainDto> content = dtos.getContent();
        List<Long> collect = content.stream().map(dto -> dto.getDeployId()).collect(Collectors.toList());
        assertThat(collect).containsExactly(deploy4.getId(), deploy5.getId(), deploy6.getId());
    }

    @Test
    //search query가 조건대로 잘 가지고 오는지에 대한 test
    void searchDeployDtosCond() {
        //given
        LocalDateTime now = LocalDateTime.now();

        Deploy deploy1 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Mugunghwa("mugunghwa1"));
        Deploy deploy2 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Mugunghwa("mugunghwa2"));
        Deploy deploy3 = new Deploy(now.plusHours(2), now.plusHours(4), "서울역", "부산역", new Saemaul("saemaul3"));
        Deploy deploy4 = new Deploy(now.plusHours(3), now.plusHours(5), "서울역", "부산역", new Saemaul("saemaul4"));
        Deploy deploy5 = new Deploy(now.plusHours(4), now.plusHours(6), "서울역", "부산역", new Ktx("ktx5"));
        Deploy deploy6 = new Deploy(now.plusHours(5), now.plusHours(7), "서울역", "부산역", new Ktx("ktx6"));

        em.persist(deploy1);
        em.persist(deploy2);
        em.persist(deploy3);
        em.persist(deploy4);
        em.persist(deploy5);
        em.persist(deploy6);

        //6개의 데이터를 넣었을 때, 0페이지에 6개니까 전부 튀어 나와야 됨
        PageRequest of = PageRequest.of(0, 6);

        //when
        //근데 도착 시간에 조건을 주어 deploy 1,2,3개만 나오게 함
        Page<DeployWithTrainDto> dtos = deploySearchRepository.searchDeployDtos(now, now.plusHours(4), of);

        //then
        //search query의 return type은 Dto고 db에 있는 값은 deploy entity이기 때문에 비교를 위해 List<Dto>를 id를 갖는 list로 map해줘야 됨
        List<DeployWithTrainDto> content = dtos.getContent();
        List<Long> collect = content.stream().map(dto -> dto.getDeployId()).collect(Collectors.toList());
        assertThat(collect).containsExactly(deploy1.getId(), deploy2.getId(), deploy3.getId());
    }
}