// File: src/main/java/com/smartoffice/facility/main/AppConstants.java
package com.smartoffice.facility.main;

import java.util.logging.Logger;

public final class AppConstants {
    public static final Logger LOGGER = Logger.getLogger(AppConstants.class.getName());

    // Time Constants
    public static final long GRACE_PERIOD_MINUTES = 5;
    public static final long GRACE_PERIOD_MS = GRACE_PERIOD_MINUTES * 60 * 1000;

    public static final long WARNING_PERIOD_MINUTES = 2;
    public static final long WARNING_PERIOD_MS = WARNING_PERIOD_MINUTES * 60 * 1000;

    public static final long SCHEDULER_DELAY_MS = 30 * 1000;

    // Occupancy/Sensor Constants
    public static final int MIN_OCCUPANCY_FOR_ACTIVATION = 2;
    // NEW: Code to trigger the emergency mode via sensor simulation
    public static final int EMERGENCY_SENSOR_CODE = 999;

    // User/Authentication Constants (for Mock Service)
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "password";

    // Menu/UI Messages - UPDATED
    public static final String MENU_HEADER = "\n--- Smart Office Facility Menu ---";
    public static final String MENU_CONFIG = "1. Configure Office (Admin Only)";
    public static final String MENU_REGISTER = "2. Register New User";
    public static final String MENU_LOGIN = "3. Login";
    public static final String MENU_BOOK = "4. Book Room";
    public static final String MENU_CANCEL = "5. Cancel Booking";
    public static final String MENU_OCCUPANCY = "6. Update Room Occupancy (Sensor Mock)";
    public static final String MENU_MANAGER_STATS = "7. View Manager Statistics (Manager Only)";
    public static final String MENU_STATUS = "8. View Facility Status";
    public static final String MENU_EMERGENCY = "9. TOGGLE EMERGENCY MODE (Override)";
    public static final String MENU_EXIT = "10. Exit";
    public static final String MENU_PROMPT = "Enter your choice: ";
    public static final String MENU_SEPARATOR = "----------------------------------";

    private AppConstants() {}
}