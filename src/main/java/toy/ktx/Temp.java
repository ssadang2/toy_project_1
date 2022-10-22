package toy.ktx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Temp {
    public static void main(String[] args) throws IOException {
        LocalDateTime a = LocalDateTime.now();
        LocalDateTime b = a;

        System.out.println("a = " + a);
        System.out.println("b = " + b);

        System.out.println("a.isAfter(b) = " + a.isAfter(b));
    }
}




