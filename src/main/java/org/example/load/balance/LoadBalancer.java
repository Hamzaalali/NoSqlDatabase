package org.example.load.balance;
import java.time.Clock;
import java.util.Arrays;

public class LoadBalancer {
    private int timeWindow=5;//in seconds
    private int maxRequests=5;
    private long[] circularBuffer;
    private long counter;
    private long lastRequestTime;//in second
    private static volatile LoadBalancer instance;
    private LoadBalancer(){
        circularBuffer=new long[timeWindow];
        Arrays.fill(circularBuffer,0);
        lastRequestTime = Clock.systemDefaultZone().millis()/1000;
        counter=0;
    }
    public static LoadBalancer getInstance() {
        LoadBalancer result = instance;
        if (result != null) {
            return result;
        }
        synchronized(LoadBalancer.class) {
            if (instance == null) {
                instance = new LoadBalancer();
            }
            return instance;
        }
    }
    public synchronized boolean addRequest(){
        long requestTime=Clock.systemDefaultZone().millis()/1000;
        update(requestTime);
        if(counter>=maxRequests)return false;
        int index= (int) (requestTime%timeWindow);
        circularBuffer[index]++;
        lastRequestTime=requestTime;
        counter++;
        return true;
    }
    private void update(long requestTime){
        long timeDifference=requestTime-lastRequestTime;
        if(timeDifference>=timeWindow){
            counter=0;
            Arrays.fill(circularBuffer,0);
        }else{
            int deletePointer= (int) ((lastRequestTime+1)%timeWindow);
            while(timeDifference>0){
                counter-=circularBuffer[deletePointer];
                circularBuffer[deletePointer]=0;
                deletePointer=(deletePointer+1)%timeWindow;
                timeDifference--;
            }
        }
    }
    public boolean isOverLoaded(){
        long requestTime=Clock.systemDefaultZone().millis()/1000;
        update(requestTime);
        if(counter>=maxRequests)return false;
        return true;
    }
}
