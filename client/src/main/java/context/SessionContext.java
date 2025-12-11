package context;

import model.dto.UserDTO;
import service.AuthService;
import service.SearchService;
import service.UserListService;

public class SessionContext {

    private static final AuthService AUTH_SERVICE = new AuthService();
    private static final SearchService SEARCH_SERVICE = new SearchService();
    private static final UserListService USER_LIST_SERVICE = new UserListService();

    private static SessionContext instance;
    private UserDTO currentUser;

    private SessionContext() {}

    public static synchronized SessionContext getInstance() {
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

    public void setUser(UserDTO user){
        this.currentUser = user;
    }

    public static AuthService getAuthService() {
        return AUTH_SERVICE;
    }

    public static SearchService getSearchService() {
        return SEARCH_SERVICE;
    }

    public static UserListService getUserListService() {
        return USER_LIST_SERVICE;
    }
}
