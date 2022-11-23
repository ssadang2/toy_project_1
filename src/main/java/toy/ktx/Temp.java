package toy.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.dto.RoomDto;
import toy.ktx.domain.ktx.KtxSeat;

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

        Class clazz = Class.forName("toy.ktx.domain.ktx.KtxSeat");
        String temp = "set" + "K1A";
        log.info("뭐지 = {}",temp);
        Method fuck = clazz.getDeclaredMethod(temp, Boolean.class);
        fuck.invoke(ktxSeat, true);

        System.out.println("ktxSeat = " + ktxSeat);
    }
}




