import java.util.*;
import java.io.*; 

// BOJ - 2484 주사위네개

public class Solution7 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Integer a = sc.nextInt();
        Integer b = sc.nextInt();
        Integer c = sc.nextInt();
        Integer d = sc.nextInt();

        solution(a, b, c, d);

        sc.close(); 
    }

    public static void solution(Integer a, Integer b, Integer c, Integer d) {
        if(a == b && a == c && b == c) {
            System.out.print(10000+(a*1000));
        } else if(a == b || a == c) {
            System.out.print(1000+(a*100));
        } else if(b == c) {
            System.out.print(1000+(b*100));
        } else {
            System.out.print((Math.max(a, Math.max(b, c))*100));
        }
    }   
}
