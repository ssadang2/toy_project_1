package toy.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<List<Boolean>> a = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();
        b.add(Boolean.TRUE);
        b.add(Boolean.TRUE);

        List<Boolean> c = new ArrayList<>();
        b.add(Boolean.TRUE);
        b.add(Boolean.TRUE);

        List<Boolean> d = new ArrayList<>();
        b.add(Boolean.TRUE);
        b.add(Boolean.TRUE);

        a.add(b);
        a.add(c);
        a.add(d);

        System.out.println("a.size() = " + a.size());

    }
}




