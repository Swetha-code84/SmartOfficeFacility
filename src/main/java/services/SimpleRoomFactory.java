package com.smartoffice.facility.services;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.controls.ACSystem;
import com.smartoffice.facility.controls.LightingSystem;
import com.smartoffice.facility.interfaces.IRoomFactory;

/**
 * Factory Method Implementation: Creates a Room object and initializes it
 * with default Observers (AC/Lights).
 */
public class SimpleRoomFactory implements IRoomFactory {
    @Override
    public Room createRoom(int roomNumber) {
        Room room = new Room(roomNumber);

        // Factory's responsibility: Attach the necessary observers
        room.registerObserver(new ACSystem(roomNumber));
        room.registerObserver(new LightingSystem(roomNumber));

        return room;
    }
}