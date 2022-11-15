package toy.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.dto.RoomDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class Temp {
    public static void main(String[] args) throws IOException {
        RoomDto roomDto = new RoomDto();
        roomDto.setRoom2("yes");

        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.convertValue(roomDto, Map.class);
        Optional first = map.values().stream().filter(v -> v != null).findAny();
        if(first.isPresent()) {
            System.out.println("first.get() = " + first.get());
        }
        System.out.println(" 맞음 ");
    }
}




