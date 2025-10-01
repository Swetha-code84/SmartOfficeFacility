package com.smartoffice.facility.interfaces;

import com.smartoffice.facility.core.Room;

/**
 * The IControlObserver interface in the Observer Pattern.
 * It defines the update method that control systems (Observers) must implement
 * to react to changes in the Room's state (Subject).
 */
public interface IControlObserver {

    /**
     * Called by the Subject (Room) to notify the Observer of a state change.
     * The Observer uses the information in the Room object to decide what action to take.
     *
     * @param room The Room object whose state has changed.
     */
    void update(Room room);
}