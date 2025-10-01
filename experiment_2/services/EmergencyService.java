// File: src/main/java/com/smartoffice.facility.services/EmergencyService.java
package com.smartoffice.facility.services;

import com.smartoffice.facility.main.AppConstants;

/**
 * Singleton Service to manage global system emergency state (e.g., Fire, Alarm).
 */
public class EmergencyService {
    private static EmergencyService instance;
    private boolean isEmergencyActive = false;

    private EmergencyService() {}

    public static synchronized EmergencyService getInstance() {
        if (instance == null) {
            instance = new EmergencyService();
        }
        return instance;
    }

    public boolean isEmergencyActive() {
        return isEmergencyActive;
    }

    /**
     * Activates the emergency state, overriding normal system operations.
     */
    public synchronized void activateEmergency() {
        if (!isEmergencyActive) {
            isEmergencyActive = true;
            AppConstants.LOGGER.severe("SYSTEM ALERT: EMERGENCY MODE ACTIVATED!");
        }
    }

    /**
     * Deactivates the emergency state, restoring normal operations.
     */
    public synchronized void deactivateEmergency() {
        if (isEmergencyActive) {
            isEmergencyActive = false;
            AppConstants.LOGGER.info("SYSTEM ALERT: EMERGENCY MODE DEACTIVATED.");
        }
    }
}