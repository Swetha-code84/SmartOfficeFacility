package com.smartoffice.facility.interfaces;

import com.smartoffice.facility.core.Booking;

public interface INotificationService {

    boolean notifyUser(Booking booking, String message);

    void notifyManagerOfExtension(String roomDetails, String requestingUser, int extensionMinutes);
}
