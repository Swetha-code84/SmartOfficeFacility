
package com.smartoffice.facility.services;

import com.smartoffice.facility.main.AppConstants;

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

    public synchronized void activateEmergency() {
        if (!isEmergencyActive) {
            isEmergencyActive = true;
            AppConstants.LOGGER.severe("SYSTEM ALERT: EMERGENCY MODE ACTIVATED!");
        }
    }

    public synchronized void deactivateEmergency() {
        if (isEmergencyActive) {
            isEmergencyActive = false;
            AppConstants.LOGGER.info("SYSTEM ALERT: EMERGENCY MODE DEACTIVATED.");
        }
    }
}
