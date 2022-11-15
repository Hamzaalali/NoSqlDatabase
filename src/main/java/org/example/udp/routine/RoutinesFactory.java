package org.example.udp.routine;

import org.example.udp.UdpRoutineTypes;

import java.util.HashMap;
import java.util.Map;

public class RoutinesFactory {
    public Map<UdpRoutineTypes,UdpRoutine> getRoutines(){
        Map<UdpRoutineTypes,UdpRoutine> routineMap=new HashMap<>();
        routineMap.put(UdpRoutineTypes.ADD_USER,new AddUserRoutine());
        return routineMap;
    }
}
