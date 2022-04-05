import java.util.*;

public class Main {
    public static void main(String[] args) {
        String str = "HelloWorld";

        System.out.println(str.matches("^[A-Za-z]*$"));

        System.out.println(str.contains("World"));
    } 
}

