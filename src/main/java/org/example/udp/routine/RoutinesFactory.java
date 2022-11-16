package org.example.udp.routine;

import org.example.udp.UdpRoutineTypes;

import java.util.HashMap;
import java.util.Map;

public class RoutinesFactory {
    public Map<UdpRoutineTypes,UdpRoutine> getRoutines(){
        Map<UdpRoutineTypes,UdpRoutine> routineMap=new HashMap<>();
        routineMap.put(UdpRoutineTypes.ADD_USER,new AddUserRoutine());
        routineMap.put(UdpRoutineTypes.SYNC,new SyncRoutine());
        routineMap.put(UdpRoutineTypes.INIT,new InitializeRoutine());
        routineMap.put(UdpRoutineTypes.QUERY_REDIRECT,new QueryRedirectRoutine());
        return routineMap;
    }
}
