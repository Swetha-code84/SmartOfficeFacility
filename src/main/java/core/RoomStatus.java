package com.smartoffice.facility.core;

/**
 * Defines the possible status states for a conference room.
 * This makes the room's current condition clear and easy to check.
 */
public enum RoomStatus {

    /**
     * The room is not booked and has fewer than 2 occupants.
     * This is the default ready-to-use state.
     */
    AVAILABLE, // <--- ADDED to resolve "cannot find symbol" errors in OfficeFacility

    /**
     * Alias for AVAILABLE (used in some legacy or simple structures).
     */
    IDLE,

    /**
     * The room has an active booking but currently has fewer than 2 occupants.
     * The 5-minute grace period timer is typically active in this state.
     */
    BOOKED,

    /**
     * The room is currently occupied by 2 or more persons.
     * Environmental controls (AC/Lights) should be ON.
     */
    OCCUPIED
}