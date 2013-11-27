import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.io.*;

class ConcurrentCounter {
    int original;
    int count;
    final ReentrantLock lock;
    final Condition change; 

    public ConcurrentCounter(int c, ReentrantLock l, Condition ch){
        original = c;
        count = c;
        lock = l;
        change = ch;
    }

    public void increment(){
        if (count + 1 <= original){
            count++;
            change.signal();
        }
    }

    public void decrement(){
        if (count - 1 >= 0){
            count--;
            change.signal();
        }
    }

    public int get(){
        return count;
    }

    public ReentrantLock getLock(){
        return lock;
    }

    public Condition getCondition(){
        return change;
    }
}
