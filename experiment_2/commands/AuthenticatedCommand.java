package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility; 
public abstract class AuthenticatedCommand implements ICommand {
    protected final OfficeFacility officeFacility;
    protected final IAuthenticationService authService;
    protected final User currentUser;
    
    public AuthenticatedCommand(OfficeFacility officeFacility, IAuthenticationService authService, User currentUser) {
        this.officeFacility = officeFacility;
        this.authService = authService;
        this.currentUser = currentUser;
    }
    @Override
    public final boolean execute() {
        if (currentUser == null) {
            System.out.println(" ERROR: You must log in to perform this action.");
            return false;
        }

        return authenticatedExecute();
    }

    protected abstract boolean authenticatedExecute();
}
