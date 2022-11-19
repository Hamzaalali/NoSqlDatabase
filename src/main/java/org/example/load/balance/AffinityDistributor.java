package org.example.load.balance;
import org.example.cluster.ClusterManager;
public class AffinityDistributor {
    private static volatile AffinityDistributor instance;
    int nodePointer;
    private AffinityDistributor(){
        nodePointer=1;
    }
    public static AffinityDistributor getInstance() {
        AffinityDistributor result = instance;
        if (result != null) {
            return result;
        }
        synchronized(AffinityDistributor.class) {
            if (instance == null) {
                instance = new AffinityDistributor();
            }
            return instance;
        }
    }
    public int hasAffinity(){
        int nodeWithAffinity=nodePointer;
        pointToNextNode();
        return nodeWithAffinity;
    }
    public void pointToNextNode(){
        nodePointer+=(nodePointer+1)%ClusterManager.getInstance().getNodesNumber();
        if(nodePointer==0){
            nodePointer=1;
        }
    }
}
