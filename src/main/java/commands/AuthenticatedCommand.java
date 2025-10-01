// SmartOfficeFacility/src/main/java/com/smartoffice/facility/commands/AuthenticatedCommand.java
package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility; // Included to resolve getAuthService() error

/**
 * Abstract base class for commands that require user authentication.
 */
public abstract class AuthenticatedCommand implements ICommand {

    // Dependencies
    protected final OfficeFacility officeFacility;
    protected final IAuthenticationService authService;
    protected final User currentUser;

    // FIX: Add the constructor expected by concrete commands
    public AuthenticatedCommand(OfficeFacility officeFacility, IAuthenticationService authService, User currentUser) {
        this.officeFacility = officeFacility;
        this.authService = authService;
        this.currentUser = currentUser;
    }

    // FIX: Change execute to return boolean, not String
    @Override
    public final boolean execute() {
        if (currentUser == null) {
            System.out.println("🔒 ERROR: You must log in to perform this action.");
            return false;
        }

        // Delegate execution to the specific subclass implementation
        return authenticatedExecute();
    }

    protected abstract boolean authenticatedExecute();
}