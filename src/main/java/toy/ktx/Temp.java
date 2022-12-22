package toy.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.RoomDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.service.KtxSeatService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class Temp {
    private static final Member temp = new Member();

    public static void main(String[] args){
        ThreadLocal<String> a = new ThreadLocal<>();
        System.out.println("a.get( = " + a.get());

    }
}




