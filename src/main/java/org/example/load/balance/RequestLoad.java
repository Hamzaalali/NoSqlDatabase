package org.example.load.balance;
import java.time.Clock;
import java.util.Arrays;

public class RequestLoad {
    private int timeWindow;//in seconds
    private int maxRequests;
    private long[] circularBuffer;
    private long counter;
    private long lastRequestTime;//in second
    private static volatile RequestLoad instance;
    private RequestLoad(){
        lastRequestTime = Clock.systemDefaultZone().millis()/1000;
        counter=0;
    }
    public static RequestLoad getInstance() {
        RequestLoad result = instance;
        if (result != null) {
            return result;
        }
        synchronized(RequestLoad.class) {
            if (instance == null) {
                instance = new RequestLoad();
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

    public void setTimeWindow(long timeWindow) {
        circularBuffer=new long[(int) timeWindow];
        Arrays.fill(circularBuffer,0);
        this.timeWindow = (int) timeWindow;
    }

    public void setMaxRequests(long maxRequests) {
        this.maxRequests = (int) maxRequests;
    }
}
