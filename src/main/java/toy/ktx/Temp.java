package toy.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        KtxSeat ktxSeat = new KtxSeat();
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.convertValue(ktxSeat, Map.class);
        System.out.println("map = " + map.size());
        System.out.println("map = " + map);

    }
}




