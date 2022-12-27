package toy.ktx;

import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.Member;

import java.util.*;

@Slf4j
public class Temp {
    private static final Member temp = new Member();

    public static void main(String[] args){
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        for (String s : b) {
            a.add(s);
        }

        System.out.println("a = " + a);


    }
}




