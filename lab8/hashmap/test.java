package hashmap;

import edu.princeton.cs.algs4.StdRandom;

public class test {

    public static void main(String[] args) {
        MyHashMap<Integer , Integer> x = new MyHashMap<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumberOfKey = StdRandom.uniform(0, 100);
            int operationNumberOfValue = StdRandom.uniform(0, 100);
            x.put(operationNumberOfKey , operationNumberOfValue);
        }
        for(int item : x){
            System.out.println(item);
        }
    }
}
