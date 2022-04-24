import java.util.*;

// BOJ - 2480 주사위세개

public class Solution6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); 
        String temp = sc.nextLine(); 
        String[] _dices = temp.split(" "); 

        Integer[] dices = new Integer[_dices.length];
        for(int i = 0; i < _dices.length; i++){
            dices[i] = Integer.parseInt(_dices[i]);
        }

        solution(dices);

        sc.close(); 
    }

    public static Integer solution(Integer[] dices) {
        HashSet<Integer> set_dices = new HashSet<>();
        for(int i = 0; i < dices.length; i++){
            set_dices.add(dices[i]);
        }
        
        Integer answer = 0;
        if(set_dices.size() == 1){
            answer = 10000 + dices[0] * 1000;
            System.out.println(answer);
            return answer;
        } else if(set_dices.size() == 2){
            answer = 1000 + dices[1] * 100;
            System.out.println(answer);
            return answer;
        } else {
            Arrays.sort(dices);
            answer = dices[dices.length - 1] * 100;
            System.out.println(answer);
            return answer;
        }
    }   
}