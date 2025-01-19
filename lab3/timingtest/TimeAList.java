package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        int[] number = {1000 , 2000 , 4000 , 8000 , 16000 , 32000 , 64000 , 128000};
        AList <Integer> Ns = new AList<Integer>() ;
        AList <Double> times = new AList<Double>();
        AList <Integer> opCounts = new AList<Integer>();
        for(int cnt : number)
        {
            Ns.addLast(cnt);
            opCounts.addLast(cnt);
        }
        timeAListConstruction(Ns , times);
        TimeAList.printTimingTable( Ns , times , opCounts);

    }

    public static void timeAListConstruction(AList<Integer> Ns, AList<Double> times) {
        for ( int i = 0 ; i < Ns.size() ; i ++)
        {
            AList<Integer>  Now = new AList<Integer>();
            double time;
            Stopwatch ee = new Stopwatch();
            for ( int j = 0 ; j < Ns.get(i) ; j ++)
            {
                Now.addLast(null);
            }
            time = ee.elapsedTime();
            times.addLast(time);
        }
    }
}

