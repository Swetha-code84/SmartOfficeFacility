package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.main.SmartOfficeApp;
import java.util.Scanner;
public class LoginCommand implements ICommand {
    private final IAuthenticationService authService;
    private final Scanner scanner;
    private final SmartOfficeApp app; 

    public LoginCommand(IAuthenticationService authService, Scanner scanner, SmartOfficeApp app) {
        this.authService = authService;
        this.scanner = scanner;
        this.app = app;
    } 
    @Override
    public boolean execute() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        User user = authService.authenticate(username, password);
        if (user != null) {
            app.setCurrentUser(user);
            System.out.println(" Login successful! Welcome, " + user.getUsername() + ".");
            return true;
        } else {
            System.out.println(" Login failed. Invalid username or password.");
            return false;
        }
    }
}
