package context;

import model.dto.UserDTO;
import service.AuthService;
import service.SearchService;
import service.UserListService;

public class SessionContext {

    private volatile AuthService authService;
    private volatile SearchService searchService;
    private volatile UserListService userListService;

    private static volatile SessionContext instance;
    private UserDTO currentUser;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        if (instance == null) {
            synchronized (SessionContext.class) {
                if (instance == null) {
                    instance = new SessionContext();
                }
            }
        }

        return instance;
    }

    public UserDTO getUser() {
        return currentUser;
    }

    public void setUser(UserDTO user) {
        this.currentUser = user;
    }

    public AuthService getAuthService() {
        if (authService == null) {
            synchronized (SessionContext.class) {
                if (authService == null) {
                    authService = new AuthService();
                }
            }
        }

        return authService;
    }

    public SearchService getSearchService() {
        if (searchService == null) {
            synchronized (SessionContext.class) {
                if (searchService == null) {
                    searchService = new SearchService();
                }
            }
        }

        return searchService;
    }

    public UserListService getUserListService() {
        if (userListService == null) {
            synchronized (SessionContext.class) {
                if (userListService == null) {
                    userListService = new UserListService();
                }
            }
        }

        return userListService;
    }
}
