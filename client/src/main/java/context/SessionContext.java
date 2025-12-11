package context;

import model.dto.UserDTO;
import service.AuthService;
import service.SearchService;
import service.UserListService;

public class SessionContext {

    private AuthService authService;
    private SearchService searchService;
    private UserListService userListService;

    private static SessionContext instance;
    private UserDTO currentUser;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }

        return instance;
    }

    public UserDTO getUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged");
        }
        return currentUser;
    }

    public void setUser(UserDTO user) {
        this.currentUser = user;
    }

    public AuthService getAuthService() {
        if (authService == null) {
            authService = new AuthService();
        }

        return authService;
    }

    public SearchService getSearchService() {
        if (searchService == null) {
            searchService = new SearchService();
        }

        return searchService;
    }

    public UserListService getUserListService() {
        if (userListService == null) {
            userListService = new UserListService();
        }

        return userListService;
    }
}
