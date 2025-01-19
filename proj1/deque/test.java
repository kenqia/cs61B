package deque;

import edu.princeton.cs.algs4.StdRandom;
import  org.junit.Test;
import static org.junit.Assert.*;
public class test {
    @Test
    public void test(){
        ArrayDeque<Integer> L = new ArrayDeque<>();

        int N = 20;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2) {   // addfirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                System.out.println("addFirst(" + randVal + ")");
            } else if (operationNumber == 3){
                L.removeFirst();
                System.out.println("removeFirst");
            } else if (operationNumber == 4 ){
                L.printDeque();
            } else if (operationNumber == 5) {
                int randVal = StdRandom.uniform(0 , 100);
                System.out.println(L.get(randVal));

            }
        }
    }
}

