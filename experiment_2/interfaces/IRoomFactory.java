package com.smartoffice.facility.interfaces;
import com.smartoffice.facility.core.Room;

public interface IRoomFactory {
    Room createRoom(int roomNumber);
}