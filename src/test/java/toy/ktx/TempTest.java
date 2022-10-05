package toy.ktx;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;

public class TempTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(A.class);

    @Test
    public void temp() {
        Member bean1 = ac.getBean(Member.class);
        Deploy bean2 = ac.getBean(Deploy.class);

        System.out.println("bean1 = " + bean1);
        System.out.println("bean2 = " + bean2);
    }

    static class A{

        @Bean
        public Member member() {
            return new Member();
        }

        @Bean
        public Deploy deploy() {
            return new Deploy();
        }
    }
}
