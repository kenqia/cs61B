package randomizedtest;

import org.junit.Test;
import static org.junit.Assert.*;

public class testThreeAddThreeRemove {
    @Test
    public  void test1()
    {
        AListNoResizing<Integer>  AListNoError = new AListNoResizing<Integer>();
        BuggyAList<Integer> AListIDontKnow = new BuggyAList<Integer>();

        AListNoError.addLast(3);
        AListNoError.addLast(33);
        AListNoError.addLast(5);

        AListIDontKnow.addLast(3);
        AListIDontKnow.addLast(33);
        AListIDontKnow.addLast(5);

        for ( int i = 0 ; i < AListNoError.size() ; i ++) {
            assertEquals(AListNoError.get(i), AListIDontKnow.get(i));
        }
        AListNoError.removeLast();
        AListIDontKnow.removeLast();

        for ( int i = 0 ; i < AListNoError.size() ; i ++) {
            assertEquals(AListNoError.get(i), AListIDontKnow.get(i));
        }
        AListNoError.removeLast();
        AListIDontKnow.removeLast();

        for ( int i = 0 ; i < AListNoError.size() ; i ++) {
            assertEquals(AListNoError.get(i), AListIDontKnow.get(i));
        }
    }
}
