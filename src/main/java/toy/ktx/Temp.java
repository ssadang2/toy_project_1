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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class Temp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<String> a = new ArrayList<>();

        a.add("roo2");
        a.add("roo3");
        Optional<String> room = a.stream().filter(r -> r.contains("room")).findAny();
        System.out.println("room = " + room.get());


    }
}




