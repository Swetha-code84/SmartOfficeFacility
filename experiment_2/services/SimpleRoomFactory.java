package com.smartoffice.facility.services;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.controls.ACSystem;
import com.smartoffice.facility.controls.LightingSystem;
import com.smartoffice.facility.interfaces.IRoomFactory;

public class SimpleRoomFactory implements IRoomFactory {
    @Override
    public Room createRoom(int roomNumber) {
        Room room = new Room(roomNumber);
        room.registerObserver(new ACSystem(roomNumber));
        room.registerObserver(new LightingSystem(roomNumber));

        return room;
    }
}
