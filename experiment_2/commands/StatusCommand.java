package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
public class StatusCommand implements ICommand {
    private final OfficeFacility officeFacility;
    public StatusCommand(OfficeFacility officeFacility) {
        this.officeFacility = officeFacility;
    }

    @Override
    public boolean execute() {
        if (!officeFacility.isConfigured()) {
            System.out.println(" The office facility has not been configured yet.");
            return false;
        }

        System.out.println("\n---  Smart Office Facility Status & Schedule ---");
        System.out.printf("%-5s | %-10s | %-9s | %-8s | %-15s | %-20s\n",
                "ROOM", "STATUS", "PEOPLE", "MAX CAP.", "OCCUPIED (SEC)", "CURRENT RESERVATION/USER");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (Room room : officeFacility.getAllRooms()) {
            Booking activeBooking = room.getCurrentActiveBooking();
            long totalSeconds = room.getTotalOccupiedDurationSeconds();
            int maxCapacity = room.getMaxCapacity();
            String currentReservationInfo = "";
            String statusDisplay = room.getStatus().toString(); 
            if (activeBooking != null) {
                
                Duration timeRemaining = Duration.between(LocalDateTime.now(), activeBooking.getEndTime());
                long minutesRemaining = timeRemaining.toMinutes();

                currentReservationInfo = String.format("ACTIVE: %s (%d min left)",
                        activeBooking.getUser().getUsername(),
                        minutesRemaining
                );
                

            } else {
                
                Booking nextBooking = room.getBookings().stream()
                        .filter(b -> b.getStartTime().isAfter(LocalDateTime.now()))
                        .min(Comparator.comparing(Booking::getStartTime))
                        .orElse(null);

                if (nextBooking != null) {
                    statusDisplay = "RESERVED";

                    currentReservationInfo = String.format("NEXT: %s at %s",
                            nextBooking.getUser().getUsername(),
                            nextBooking.getStartTime().toLocalTime()
                    );
                } else {
                    currentReservationInfo = "Available to Book";
                    statusDisplay = "AVAILABLE";
                }
            }

            List<Booking> upcomingBookings = room.getBookings().stream()
                    .filter(b -> b.getStartTime().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStartTime))
                    .collect(Collectors.toList());

            System.out.printf("%-5d | %-10s | %-9d | %-8d | %-15d | %-20s\n",
                    room.getRoomNumber(),
                    statusDisplay,
                    room.getOccupancyCount(),
                    maxCapacity,
                    totalSeconds,
                    currentReservationInfo
            );

            if (!upcomingBookings.isEmpty()) {
                System.out.println("      > Upcoming Bookings (Top 3):");
                upcomingBookings.stream().limit(3).forEach(booking -> {
                    System.out.printf("      >   %s-%s by %s\n",
                            booking.getStartTime().toLocalTime(),
                            booking.getEndTime().toLocalTime(),
                            booking.getUser().getUsername()
                    );
                });
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        return true;
    }
}
