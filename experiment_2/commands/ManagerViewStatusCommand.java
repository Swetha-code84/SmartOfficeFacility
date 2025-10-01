package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility;
import java.util.Scanner;
public class ManagerViewStatusCommand extends AuthenticatedCommand {
    private final StatusCommand statusCommand;
    private final Scanner scanner;
    public ManagerViewStatusCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
        this.statusCommand = new StatusCommand(officeFacility);
    }
    @Override
    protected boolean authenticatedExecute() {
        if (!currentUser.getUsername().equalsIgnoreCase("manager")) {
            System.out.println(" Access denied. This option is reserved for the 'manager' user role.");
            return false;
        }
        System.out.println("\n---  MANAGER ID VERIFICATION ---");
        System.out.print("Enter Manager ID for authorization: ");
        String enteredManagerId = scanner.nextLine().trim();
        
        if (currentUser.getManagerId() != null && currentUser.getManagerId().equals(enteredManagerId)) {
            System.out.println(" ID Verified. Loading Manager Dashboard...");
            System.out.println("----------------------------------------");
            return statusCommand.execute(); 

        } else {
            System.out.println(" Command Failed: Invalid Manager ID or you are not the correct person.");
            return false;
        }
    }
}
