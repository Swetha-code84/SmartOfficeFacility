package com.smartoffice.facility.interfaces;

import com.smartoffice.facility.core.User;

public interface IAuthenticationService {

    User authenticate(String username, String password);

    boolean isAdmin(User user);

    User getCurrentUser();

    User registerUser(String username, String userId, String password);
}
